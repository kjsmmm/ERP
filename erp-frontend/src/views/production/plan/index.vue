<template>
  <div>
    <div class="table-header">
      <h3>生产计划</h3>
      <el-button type="primary" @click="handleAdd">新增计划</el-button>
    </div>

    <div class="search-bar">
      <el-select v-model="filterStatus" placeholder="按状态筛选" clearable style="width: 150px" @change="loadData">
        <el-option label="草稿" :value="0" />
        <el-option label="已下达" :value="1" />
        <el-option label="执行中" :value="2" />
        <el-option label="已完成" :value="3" />
      </el-select>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="planCode" label="计划编码" width="120" />
      <el-table-column prop="productName" label="产品" min-width="150" />
      <el-table-column prop="plannedQty" label="计划数量" width="100" />
      <el-table-column prop="startDate" label="开始日期" width="110" />
      <el-table-column prop="endDate" label="结束日期" width="110" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="['info', 'warning', 'primary', 'success'][row.status]">
            {{ ['草稿', '已下达', '执行中', '已完成'][row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="260">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)" v-if="row.status === 0">编辑</el-button>
          <el-button link type="success" @click="handleRelease(row)" v-if="row.status === 0">下达</el-button>
          <el-button link type="primary" @click="handleStart(row)" v-if="row.status === 1">执行</el-button>
          <el-button link type="warning" @click="handleComplete(row)" v-if="row.status === 2">完成</el-button>
          <el-button link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑计划' : '新增计划'" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="计划编码" prop="planCode">
          <el-input v-model="formData.planCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="产品" prop="productId">
          <el-select v-model="formData.productId" filterable placeholder="选择产品" style="width: 100%">
            <el-option v-for="p in productList" :key="p.id" :label="`${p.productCode} - ${p.productName}`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划数量" prop="plannedQty">
          <el-input-number v-model="formData.plannedQty" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="formData.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="formData.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getProductionPlanPage, createProductionPlan, updateProductionPlan, deleteProductionPlan, releaseProductionPlan, startProductionPlan, completeProductionPlan } from '@/api/production/productionPlan';
import { getProductPage } from '@/api/product/product';
import type { ProductionPlan, ProductionPlanFormData } from '@/types/production';
import type { Product } from '@/types/product';

const loading = ref(false);
const tableData = ref<ProductionPlan[]>([]);
const productList = ref<Product[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const editId = ref(0);
const filterStatus = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const formData = reactive<ProductionPlanFormData>({
  planCode: '',
  productId: undefined as any,
  plannedQty: 1,
  startDate: '',
  endDate: '',
  remark: '',
});

const formRules = {
  planCode: [{ required: true, message: '请输入计划编码', trigger: 'blur' }],
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  plannedQty: [{ required: true, message: '请输入计划数量', trigger: 'blur' }],
};

onMounted(async () => {
  const res = await getProductPage({ pageSize: 1000 });
  productList.value = res.data.records;
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getProductionPlanPage({ status: filterStatus.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { planCode: '', productId: undefined, plannedQty: 1, startDate: '', endDate: '', remark: '' });
  dialogVisible.value = true;
}

function handleEdit(row: ProductionPlan) {
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(formData, { planCode: row.planCode, productId: row.productId, plannedQty: row.plannedQty, startDate: row.startDate, endDate: row.endDate, remark: row.remark });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (isEdit.value) {
    await updateProductionPlan(editId.value, formData);
    ElMessage.success('更新成功');
  } else {
    await createProductionPlan(formData);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(row: ProductionPlan) {
  await ElMessageBox.confirm('确认删除该计划？', '提示', { type: 'warning' });
  await deleteProductionPlan(row.id);
  ElMessage.success('删除成功');
  loadData();
}

async function handleRelease(row: ProductionPlan) {
  await ElMessageBox.confirm('确认下达该计划？', '提示', { type: 'warning' });
  await releaseProductionPlan(row.id);
  ElMessage.success('下达成功');
  loadData();
}

async function handleStart(row: ProductionPlan) {
  await startProductionPlan(row.id);
  ElMessage.success('已开始执行');
  loadData();
}

async function handleComplete(row: ProductionPlan) {
  await ElMessageBox.confirm('确认标记该计划为已完成？', '提示', { type: 'warning' });
  await completeProductionPlan(row.id);
  ElMessage.success('已完成');
  loadData();
}
</script>
