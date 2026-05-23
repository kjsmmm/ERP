<template>
  <div>
    <div class="table-header">
      <h3>采购入库</h3>
      <el-button type="primary" @click="handleAdd">新增入库</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索入库单号" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData">
        <template #append>
          <el-button @click="loadData">搜索</el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="receiptNo" label="入库单号" width="150" />
      <el-table-column prop="orderNo" label="采购单号" width="150" />
      <el-table-column prop="warehouseName" label="仓库" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已入库' : '待入库' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="检验状态" width="100">
        <template #default="{ row }">
          <el-tag :type="inspectionType(row.inspectionStatus)">{{ inspectionText(row.inspectionStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" fixed="right" width="100">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <!-- 新增入库对话框 -->
    <el-dialog v-model="dialogVisible" title="新增采购入库" width="700px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="采购单" prop="purchaseOrderId">
          <el-select v-model="formData.purchaseOrderId" filterable placeholder="选择采购单" style="width: 300px" @change="handleOrderChange">
            <el-option v-for="o in orderList" :key="o.id" :label="o.orderNo" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="仓库" prop="warehouseId">
          <el-select v-model="formData.warehouseId" filterable placeholder="选择仓库" style="width: 300px">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>

        <el-divider content-position="left">入库明细</el-divider>

        <div v-for="(item, index) in formData.items" :key="index" style="display: flex; gap: 10px; margin-bottom: 10px; align-items: flex-start;">
          <el-form-item :prop="`items.${index}.productId`" :rules="[{ required: true, message: '请选择产品', trigger: 'change' }]" style="margin-bottom: 0;">
            <el-select v-model="item.productId" filterable placeholder="选择产品" style="width: 200px">
              <el-option v-for="p in orderProductList" :key="p.productId" :label="p.productName" :value="p.productId" />
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
    <el-dialog v-model="detailVisible" title="入库单详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="入库单号">{{ currentReceipt?.receiptNo }}</el-descriptions-item>
        <el-descriptions-item label="采购单号">{{ currentReceipt?.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ currentReceipt?.warehouseName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentReceipt?.status === 1 ? 'success' : 'info'">{{ currentReceipt?.status === 1 ? '已入库' : '待入库' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="检验状态">
          <el-tag :type="inspectionType(currentReceipt?.inspectionStatus)">{{ inspectionText(currentReceipt?.inspectionStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentReceipt?.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentReceipt?.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <h4 style="margin: 16px 0 8px">入库明细</h4>
      <el-table :data="currentReceipt?.items || []" border size="small">
        <el-table-column prop="productName" label="产品" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, type FormInstance } from 'element-plus';
import { getPurchaseReceiptPage, createPurchaseReceipt } from '@/api/purchase/purchaseReceipt';
import { getPurchaseOrderPage } from '@/api/purchase/purchaseOrder';
import { getWarehouseList } from '@/api/inventory/warehouse';
import type { PurchaseReceipt, PurchaseReceiptFormData } from '@/types/purchase';

const loading = ref(false);
const tableData = ref<PurchaseReceipt[]>([]);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const formRef = ref<FormInstance>();
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const currentReceipt = ref<PurchaseReceipt | null>(null);
const orderList = ref<{ id: number; orderNo: string }[]>([]);
const warehouseList = ref<{ id: number; warehouseName: string }[]>([]);
const orderProductList = ref<{ productId: number; productName: string; quantity: number }[]>([]);

const formData = reactive<PurchaseReceiptFormData>({
  purchaseOrderId: 0,
  warehouseId: 0,
  remark: '',
  items: [{ productId: 0, quantity: 1, unit: '', remark: '' }],
});

const formRules = {
  purchaseOrderId: [{ required: true, message: '请选择采购单', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
};

onMounted(() => {
  loadData();
  loadDropdowns();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getPurchaseReceiptPage({ keyword: keyword.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

async function loadDropdowns() {
  const [orderRes, warehouseRes] = await Promise.all([
    getPurchaseOrderPage({ status: 1, pageNum: 1, pageSize: 999 }),
    getWarehouseList(),
  ]);
  orderList.value = orderRes.data.records.map(o => ({ id: o.id, orderNo: o.orderNo }));
  warehouseList.value = warehouseRes.data.map(w => ({ id: w.id, warehouseName: w.warehouseName }));
}

function handleAdd() {
  Object.assign(formData, { purchaseOrderId: 0, warehouseId: 0, remark: '', items: [{ productId: 0, quantity: 1, unit: '', remark: '' }] });
  orderProductList.value = [];
  dialogVisible.value = true;
}

function handleOrderChange(orderId: number) {
  // 从采购单明细加载产品列表
  const orderRes = orderList.value.find(o => o.id === orderId);
  if (orderRes) {
    // 简化处理：使用产品列表
    orderProductList.value = [];
  }
}

async function handleCreate() {
  if (!formRef.value) return;
  await formRef.value.validate();
  await createPurchaseReceipt(formData);
  ElMessage.success('入库成功');
  dialogVisible.value = false;
  loadData();
}

function handleView(row: PurchaseReceipt) {
  currentReceipt.value = row;
  detailVisible.value = true;
}

function inspectionText(status?: number) {
  const map: Record<number, string> = { 0: '待检验', 1: '合格', 2: '不合格' };
  return status !== undefined ? map[status] || '未知' : '';
}

function inspectionType(status?: number) {
  const map: Record<number, string> = { 0: 'info', 1: 'success', 2: 'danger' };
  return status !== undefined ? map[status] || 'info' : 'info';
}
</script>
