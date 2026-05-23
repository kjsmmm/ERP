<template>
  <div>
    <div class="table-header">
      <h3>采购申请</h3>
      <el-button type="primary" @click="handleAdd">新增申请</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索申请编号" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
      <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 120px; margin-left: 10px" @change="loadData">
        <el-option label="草稿" :value="0" />
        <el-option label="审批中" :value="1" />
        <el-option label="已通过" :value="2" />
        <el-option label="已驳回" :value="3" />
      </el-select>
      <el-button style="margin-left: 10px" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="requestNo" label="申请编号" width="150" />
      <el-table-column label="申请类型" width="100">
        <template #default="{ row }">
          {{ row.requestType === 1 ? '原材料' : row.requestType === 2 ? '耗材' : '其他' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
          <el-button v-if="row.status === 0" link type="success" @click="handleSubmit(row)">提交审批</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <!-- 新增采购申请对话框 -->
    <el-dialog v-model="dialogVisible" title="新增采购申请" width="700px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="申请类型" prop="requestType">
          <el-select v-model="formData.requestType" placeholder="请选择">
            <el-option label="原材料" :value="1" />
            <el-option label="耗材" :value="2" />
            <el-option label="其他" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>

        <el-divider content-position="left">申请明细</el-divider>

        <div v-for="(item, index) in formData.items" :key="index" style="display: flex; gap: 10px; margin-bottom: 10px; align-items: flex-start;">
          <el-form-item :prop="`items.${index}.productId`" :rules="[{ required: true, message: '请选择产品', trigger: 'change' }]" style="margin-bottom: 0;">
            <el-select v-model="item.productId" filterable placeholder="选择产品" style="width: 200px">
              <el-option v-for="p in productList" :key="p.id" :label="p.productName" :value="p.id" />
            </el-select>
          </el-form-item>
          <el-form-item :prop="`items.${index}.quantity`" :rules="[{ required: true, message: '请输入数量', trigger: 'blur' }]" style="margin-bottom: 0;">
            <el-input-number v-model="item.quantity" :min="1" placeholder="数量" style="width: 120px" />
          </el-form-item>
          <el-form-item style="margin-bottom: 0;">
            <el-input v-model="item.unit" placeholder="单位" style="width: 80px" />
          </el-form-item>
          <el-form-item style="margin-bottom: 0;">
            <el-input v-model="item.remark" placeholder="备注" style="width: 150px" />
          </el-form-item>
          <el-button v-if="formData.items.length > 1" type="danger" link @click="formData.items.splice(index, 1)">删除</el-button>
        </div>
        <el-button type="primary" link @click="formData.items.push({ productId: 0, quantity: 1, unit: '', remark: '' })">+ 添加明细</el-button>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailVisible" title="采购申请详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号">{{ currentRequest?.requestNo }}</el-descriptions-item>
        <el-descriptions-item label="申请类型">{{ currentRequest?.requestType === 1 ? '原材料' : currentRequest?.requestType === 2 ? '耗材' : '其他' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(currentRequest?.status)">{{ statusText(currentRequest?.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRequest?.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRequest?.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <h4 style="margin: 16px 0 8px">申请明细</h4>
      <el-table :data="currentRequest?.items || []" border size="small">
        <el-table-column prop="productName" label="产品" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getPurchaseRequestPage, createPurchaseRequest, submitPurchaseRequest } from '@/api/purchase/purchaseRequest';
import { getProductPage } from '@/api/product/product';
import type { PurchaseRequest, PurchaseRequestFormData } from '@/types/purchase';

const loading = ref(false);
const tableData = ref<PurchaseRequest[]>([]);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const formRef = ref<FormInstance>();
const keyword = ref('');
const statusFilter = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const currentRequest = ref<PurchaseRequest | null>(null);
const productList = ref<{ id: number; productName: string }[]>([]);

const formData = reactive<PurchaseRequestFormData>({
  requestType: 1,
  remark: '',
  items: [{ productId: 0, quantity: 1, unit: '', remark: '' }],
});

const formRules = {
  requestType: [{ required: true, message: '请选择申请类型', trigger: 'change' }],
};

onMounted(() => {
  loadData();
  loadProducts();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getPurchaseRequestPage({ keyword: keyword.value, status: statusFilter.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

async function loadProducts() {
  const res = await getProductPage({ pageNum: 1, pageSize: 999 });
  productList.value = res.data.records.map(p => ({ id: p.id, productName: p.productName }));
}

function handleAdd() {
  Object.assign(formData, { requestType: 1, remark: '', items: [{ productId: 0, quantity: 1, unit: '', remark: '' }] });
  dialogVisible.value = true;
}

async function handleCreate() {
  if (!formRef.value) return;
  await formRef.value.validate();
  await createPurchaseRequest(formData);
  ElMessage.success('创建成功');
  dialogVisible.value = false;
  loadData();
}

async function handleSubmit(row: PurchaseRequest) {
  await ElMessageBox.confirm('确认提交审批？', '提示', { type: 'warning' });
  await submitPurchaseRequest(row.id);
  ElMessage.success('已提交审批');
  loadData();
}

function handleView(row: PurchaseRequest) {
  currentRequest.value = row;
  detailVisible.value = true;
}

function statusText(status?: number) {
  const map: Record<number, string> = { 0: '草稿', 1: '审批中', 2: '已通过', 3: '已驳回' };
  return status !== undefined ? map[status] || '未知' : '';
}

function statusType(status?: number) {
  const map: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' };
  return status !== undefined ? map[status] || 'info' : 'info';
}
</script>
