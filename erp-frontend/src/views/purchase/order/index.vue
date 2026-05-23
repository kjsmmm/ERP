<template>
  <div>
    <div class="table-header">
      <h3>采购单</h3>
      <el-button type="primary" @click="handleAdd">新增采购单</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索采购单号" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
      <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 120px; margin-left: 10px" @change="loadData">
        <el-option label="草稿" :value="0" />
        <el-option label="已确认" :value="1" />
        <el-option label="部分入库" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
      <el-button style="margin-left: 10px" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="采购单号" width="150" />
      <el-table-column prop="supplierName" label="供应商" width="150" />
      <el-table-column prop="requestNo" label="关联申请" width="150" />
      <el-table-column prop="totalAmount" label="总金额" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
          <el-button v-if="row.status === 0" link type="success" @click="handleConfirm(row)">确认</el-button>
          <el-button v-if="row.status === 0 || row.status === 1" link type="danger" @click="handleCancel(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <!-- 新增采购单对话框 -->
    <el-dialog v-model="dialogVisible" title="新增采购单" width="800px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="供应商" prop="supplierId">
          <el-select v-model="formData.supplierId" filterable placeholder="选择供应商" style="width: 300px">
            <el-option v-for="s in supplierList" :key="s.id" :label="s.supplierName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联申请">
          <el-select v-model="formData.purchaseRequestId" filterable clearable placeholder="选择采购申请（可选）" style="width: 300px">
            <el-option v-for="r in requestList" :key="r.id" :label="r.requestNo" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>

        <el-divider content-position="left">采购明细</el-divider>

        <div v-for="(item, index) in formData.items" :key="index" style="display: flex; gap: 10px; margin-bottom: 10px; align-items: flex-start;">
          <el-form-item :prop="`items.${index}.productId`" :rules="[{ required: true, message: '请选择产品', trigger: 'change' }]" style="margin-bottom: 0;">
            <el-select v-model="item.productId" filterable placeholder="选择产品" style="width: 180px">
              <el-option v-for="p in productList" :key="p.id" :label="p.productName" :value="p.id" />
            </el-select>
          </el-form-item>
          <el-form-item :prop="`items.${index}.quantity`" :rules="[{ required: true, message: '请输入数量', trigger: 'blur' }]" style="margin-bottom: 0;">
            <el-input-number v-model="item.quantity" :min="1" placeholder="数量" style="width: 100px" />
          </el-form-item>
          <el-form-item :prop="`items.${index}.unitPrice`" :rules="[{ required: true, message: '请输入单价', trigger: 'blur' }]" style="margin-bottom: 0;">
            <el-input-number v-model="item.unitPrice" :min="0" :precision="2" placeholder="单价" style="width: 120px" />
          </el-form-item>
          <el-form-item style="margin-bottom: 0;">
            <el-input v-model="item.unit" placeholder="单位" style="width: 70px" />
          </el-form-item>
          <el-form-item style="margin-bottom: 0;">
            <el-input v-model="item.remark" placeholder="备注" style="width: 120px" />
          </el-form-item>
          <el-button v-if="formData.items.length > 1" type="danger" link @click="formData.items.splice(index, 1)">删除</el-button>
        </div>
        <el-button type="primary" link @click="formData.items.push({ productId: 0, quantity: 1, unitPrice: 0, unit: '', remark: '' })">+ 添加明细</el-button>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailVisible" title="采购单详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="采购单号">{{ currentOrder?.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ currentOrder?.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="关联申请">{{ currentOrder?.requestNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ currentOrder?.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(currentOrder?.status)">{{ statusText(currentOrder?.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder?.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder?.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <h4 style="margin: 16px 0 8px">采购明细</h4>
      <el-table :data="currentOrder?.items || []" border size="small">
        <el-table-column prop="productName" label="产品" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="80" />
        <el-table-column prop="amount" label="金额" width="100" />
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getPurchaseOrderPage, createPurchaseOrder, confirmPurchaseOrder, cancelPurchaseOrder } from '@/api/purchase/purchaseOrder';
import { getSupplierList } from '@/api/purchase/supplier';
import { getPurchaseRequestPage } from '@/api/purchase/purchaseRequest';
import { getProductPage } from '@/api/product/product';
import type { PurchaseOrder, PurchaseOrderFormData } from '@/types/purchase';

const loading = ref(false);
const tableData = ref<PurchaseOrder[]>([]);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const formRef = ref<FormInstance>();
const keyword = ref('');
const statusFilter = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const currentOrder = ref<PurchaseOrder | null>(null);
const supplierList = ref<{ id: number; supplierName: string }[]>([]);
const requestList = ref<{ id: number; requestNo: string }[]>([]);
const productList = ref<{ id: number; productName: string }[]>([]);

const formData = reactive<PurchaseOrderFormData>({
  supplierId: 0,
  purchaseRequestId: undefined,
  remark: '',
  items: [{ productId: 0, quantity: 1, unitPrice: 0, unit: '', remark: '' }],
});

const formRules = {
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
};

onMounted(() => {
  loadData();
  loadDropdowns();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getPurchaseOrderPage({ keyword: keyword.value, status: statusFilter.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

async function loadDropdowns() {
  const [supplierRes, requestRes, productRes] = await Promise.all([
    getSupplierList(),
    getPurchaseRequestPage({ status: 2, pageNum: 1, pageSize: 999 }),
    getProductPage({ pageNum: 1, pageSize: 999 }),
  ]);
  supplierList.value = supplierRes.data.map(s => ({ id: s.id, supplierName: s.supplierName }));
  requestList.value = requestRes.data.records.map(r => ({ id: r.id, requestNo: r.requestNo }));
  productList.value = productRes.data.records.map(p => ({ id: p.id, productName: p.productName }));
}

function handleAdd() {
  Object.assign(formData, { supplierId: 0, purchaseRequestId: undefined, remark: '', items: [{ productId: 0, quantity: 1, unitPrice: 0, unit: '', remark: '' }] });
  dialogVisible.value = true;
}

async function handleCreate() {
  if (!formRef.value) return;
  await formRef.value.validate();
  await createPurchaseOrder(formData);
  ElMessage.success('创建成功');
  dialogVisible.value = false;
  loadData();
}

async function handleConfirm(row: PurchaseOrder) {
  await ElMessageBox.confirm('确认该采购单？', '提示', { type: 'warning' });
  await confirmPurchaseOrder(row.id);
  ElMessage.success('已确认');
  loadData();
}

async function handleCancel(row: PurchaseOrder) {
  await ElMessageBox.confirm('确认取消该采购单？', '提示', { type: 'warning' });
  await cancelPurchaseOrder(row.id);
  ElMessage.success('已取消');
  loadData();
}

function handleView(row: PurchaseOrder) {
  currentOrder.value = row;
  detailVisible.value = true;
}

function statusText(status?: number) {
  const map: Record<number, string> = { 0: '草稿', 1: '已确认', 2: '部分入库', 3: '已完成', 4: '已取消' };
  return status !== undefined ? map[status] || '未知' : '';
}

function statusType(status?: number) {
  const map: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'warning', 3: 'success', 4: 'danger' };
  return status !== undefined ? map[status] || 'info' : 'info';
}
</script>
