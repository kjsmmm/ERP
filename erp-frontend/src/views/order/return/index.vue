<template>
  <div>
    <div class="table-header">
      <h3>销售退货</h3>
      <el-button type="primary" @click="handleAdd">新增退货单</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索退货单号" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
      <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px; margin-left: 10px" @change="loadData">
        <el-option label="待审批" :value="0" />
        <el-option label="审批中" :value="1" />
        <el-option label="已通过" :value="2" />
        <el-option label="已驳回" :value="3" />
        <el-option label="已入库" :value="4" />
      </el-select>
      <el-button style="margin-left: 10px" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="returnNo" label="退货单号" width="160" />
      <el-table-column prop="deliveryNo" label="原发货单" width="150" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="returnReason" label="退货原因" min-width="120" show-overflow-tooltip />
      <el-table-column prop="remark" label="备注" min-width="100" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" fixed="right" width="160">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" link type="success" @click="handleSubmit(row)">提交审批</el-button>
          <el-button v-if="row.status === 2" link type="warning" @click="handleReceive(row)">退货入库</el-button>
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <el-dialog v-model="dialogVisible" title="新增退货单" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="原发货单" prop="deliveryId">
          <el-input-number v-model="formData.deliveryId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="退货原因">
          <el-input v-model="formData.returnReason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="入库仓库">
          <el-input-number v-model="formData.warehouseId" :min="1" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
        <el-divider>退货明细</el-divider>
        <div v-for="(item, idx) in formData.items" :key="idx" class="item-row">
          <el-form-item :label="'产品' + (idx + 1)" :prop="'items.' + idx + '.productId'" :rules="{ required: true, message: '请输入产品ID', trigger: 'blur' }">
            <el-input-number v-model="item.productId" :min="1" />
          </el-form-item>
          <el-form-item :prop="'items.' + idx + '.quantity'" :rules="{ required: true, message: '请输入数量', trigger: 'blur' }">
            <el-input-number v-model="item.quantity" :min="1" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="item.reason" placeholder="退货原因" />
          </el-form-item>
          <el-button link type="danger" @click="formData.items.splice(idx, 1)">删除</el-button>
        </div>
        <el-button type="primary" link @click="formData.items.push({ productId: 0, quantity: 0, reason: '' })">+ 添加明细</el-button>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="退货单详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="退货单号">{{ currentRecord?.returnNo }}</el-descriptions-item>
        <el-descriptions-item label="原发货单">{{ currentRecord?.deliveryNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退货原因">{{ currentRecord?.returnReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(currentRecord?.status)">{{ statusText(currentRecord?.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRecord?.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getReturnPage, createReturn, submitReturn, receiveReturn } from '@/api/order/salesReturn';
import type { SalesReturn, SalesReturnFormData } from '@/types/order';
import { RETURN_STATUS_MAP } from '@/types/order';

const loading = ref(false);
const tableData = ref<SalesReturn[]>([]);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const formRef = ref<FormInstance>();
const keyword = ref('');
const statusFilter = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const currentRecord = ref<SalesReturn | null>(null);

const formData = reactive<SalesReturnFormData>({
  deliveryId: 0,
  returnReason: '',
  warehouseId: 0,
  remark: '',
  items: [{ productId: 0, quantity: 0, reason: '' }],
});

const formRules = {
  deliveryId: [{ required: true, message: '请输入原发货单ID', trigger: 'blur' }],
};

onMounted(() => loadData());

async function loadData() {
  loading.value = true;
  try {
    const res = await getReturnPage({ keyword: keyword.value, status: statusFilter.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  Object.assign(formData, { deliveryId: 0, returnReason: '', warehouseId: 0, remark: '', items: [{ productId: 0, quantity: 0, reason: '' }] });
  dialogVisible.value = true;
}

async function handleCreate() {
  if (!formRef.value) return;
  await formRef.value.validate();
  await createReturn({ ...formData });
  ElMessage.success('创建成功');
  dialogVisible.value = false;
  loadData();
}

async function handleSubmit(row: SalesReturn) {
  await ElMessageBox.confirm('确认提交审批？', '提示', { type: 'warning' });
  await submitReturn(row.id);
  ElMessage.success('已提交审批');
  loadData();
}

async function handleReceive(row: SalesReturn) {
  await ElMessageBox.confirm('确认退货入库？将增加库存。', '提示', { type: 'warning' });
  await receiveReturn(row.id);
  ElMessage.success('退货入库成功');
  loadData();
}

function handleView(row: SalesReturn) {
  currentRecord.value = row;
  detailVisible.value = true;
}

function statusText(status?: number) {
  if (status === undefined) return '';
  return RETURN_STATUS_MAP[status]?.label || '未知';
}

function statusType(status?: number) {
  if (status === undefined) return 'info';
  return (RETURN_STATUS_MAP[status]?.type || 'info') as 'info' | 'warning' | 'success' | 'danger' | '';
}
</script>

<style scoped>
.item-row {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
</style>
