<template>
  <div>
    <div class="table-header">
      <h3>销售发货</h3>
      <el-button type="primary" @click="handleAdd">新增发货单</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索发货单号" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
      <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px; margin-left: 10px" @change="loadData">
        <el-option label="草稿" :value="0" />
        <el-option label="待出库" :value="1" />
        <el-option label="已出库" :value="2" />
        <el-option label="已签收" :value="3" />
      </el-select>
      <el-button style="margin-left: 10px" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="deliveryNo" label="发货单号" width="160" />
      <el-table-column prop="orderNo" label="订单编号" width="150" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="deliveryDate" label="发货日期" width="120" />
      <el-table-column prop="logisticsCompany" label="物流公司" width="120" />
      <el-table-column prop="trackingNo" label="运单号" width="160" />
      <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" link type="success" @click="handlePick(row)">拣货</el-button>
          <el-button v-if="row.status === 1" link type="warning" @click="handleShipOut(row)">出库</el-button>
          <el-button v-if="row.status === 2" link type="success" @click="handleSign(row)">签收</el-button>
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <el-dialog v-model="dialogVisible" title="新增发货单" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="销售订单" prop="orderId">
          <el-input-number v-model="formData.orderId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="发货日期">
          <el-date-picker v-model="formData.deliveryDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="仓库ID">
          <el-input-number v-model="formData.warehouseId" :min="1" />
        </el-form-item>
        <el-form-item label="物流公司">
          <el-input v-model="formData.logisticsCompany" />
        </el-form-item>
        <el-form-item label="运单号">
          <el-input v-model="formData.trackingNo" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
        <el-divider>发货明细</el-divider>
        <div v-for="(item, idx) in formData.items" :key="idx" class="item-row">
          <el-form-item :label="'产品' + (idx + 1)" :prop="'items.' + idx + '.productId'" :rules="{ required: true, message: '请输入产品ID', trigger: 'blur' }">
            <el-input-number v-model="item.productId" :min="1" />
          </el-form-item>
          <el-form-item :prop="'items.' + idx + '.quantity'" :rules="{ required: true, message: '请输入数量', trigger: 'blur' }">
            <el-input-number v-model="item.quantity" :min="1" />
          </el-form-item>
          <el-button link type="danger" @click="formData.items.splice(idx, 1)">删除</el-button>
        </div>
        <el-button type="primary" link @click="formData.items.push({ productId: 0, quantity: 0 })">+ 添加明细</el-button>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="发货单详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="发货单号">{{ currentRecord?.deliveryNo }}</el-descriptions-item>
        <el-descriptions-item label="订单编号">{{ currentRecord?.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="物流公司">{{ currentRecord?.logisticsCompany || '-' }}</el-descriptions-item>
        <el-descriptions-item label="运单号">{{ currentRecord?.trackingNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(currentRecord?.status)">{{ statusText(currentRecord?.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发货日期">{{ currentRecord?.deliveryDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRecord?.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getDeliveryPage, createDelivery, pickDelivery, shipOutDelivery, signDelivery } from '@/api/order/salesDelivery';
import type { SalesDelivery, SalesDeliveryFormData } from '@/types/order';
import { DELIVERY_STATUS_MAP } from '@/types/order';

const loading = ref(false);
const tableData = ref<SalesDelivery[]>([]);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const formRef = ref<FormInstance>();
const keyword = ref('');
const statusFilter = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const currentRecord = ref<SalesDelivery | null>(null);

const formData = reactive<SalesDeliveryFormData>({
  orderId: 0,
  deliveryDate: '',
  logisticsCompany: '',
  trackingNo: '',
  warehouseId: 0,
  remark: '',
  items: [{ productId: 0, quantity: 0 }],
});

const formRules = {
  orderId: [{ required: true, message: '请输入销售订单ID', trigger: 'blur' }],
};

onMounted(() => loadData());

async function loadData() {
  loading.value = true;
  try {
    const res = await getDeliveryPage({ keyword: keyword.value, status: statusFilter.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  Object.assign(formData, { orderId: 0, deliveryDate: '', logisticsCompany: '', trackingNo: '', warehouseId: 0, remark: '', items: [{ productId: 0, quantity: 0 }] });
  dialogVisible.value = true;
}

async function handleCreate() {
  if (!formRef.value) return;
  await formRef.value.validate();
  await createDelivery({ ...formData, deliveryDate: formData.deliveryDate || undefined });
  ElMessage.success('创建成功');
  dialogVisible.value = false;
  loadData();
}

async function handlePick(row: SalesDelivery) {
  await ElMessageBox.confirm('确认拣货完成？', '提示', { type: 'warning' });
  await pickDelivery(row.id);
  ElMessage.success('拣货完成');
  loadData();
}

async function handleShipOut(row: SalesDelivery) {
  await ElMessageBox.confirm('确认出库？将扣减库存。', '提示', { type: 'warning' });
  await shipOutDelivery(row.id);
  ElMessage.success('出库成功');
  loadData();
}

async function handleSign(row: SalesDelivery) {
  await ElMessageBox.confirm('确认客户已签收？', '提示', { type: 'warning' });
  await signDelivery(row.id);
  ElMessage.success('已签收');
  loadData();
}

function handleView(row: SalesDelivery) {
  currentRecord.value = row;
  detailVisible.value = true;
}

function statusText(status?: number) {
  if (status === undefined) return '';
  return DELIVERY_STATUS_MAP[status]?.label || '未知';
}

function statusType(status?: number) {
  if (status === undefined) return 'info';
  return (DELIVERY_STATUS_MAP[status]?.type || 'info') as 'info' | 'warning' | 'success' | 'danger' | '';
}
</script>

<style scoped>
.item-row {
  display: flex;
  gap: 10px;
  align-items: center;
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
