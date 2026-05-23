<template>
  <div>
    <div class="table-header">
      <h3>应收账款</h3>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索应收单号" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
      <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px; margin-left: 10px" @change="loadData">
        <el-option label="未收" :value="0" />
        <el-option label="部分收" :value="1" />
        <el-option label="已收" :value="2" />
      </el-select>
      <el-button style="margin-left: 10px" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="receivableNo" label="应收单号" width="160" />
      <el-table-column prop="customerName" label="客户" width="120" />
      <el-table-column label="应收金额" width="120">
        <template #default="{ row }">{{ row.amount?.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="已收金额" width="120">
        <template #default="{ row }">{{ row.paidAmount?.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="arStatusType(row.status)">{{ arStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发票" width="100">
        <template #default="{ row }">
          {{ row.invoiceStatus === 1 ? '已开票' : '未开票' }}
        </template>
      </el-table-column>
      <el-table-column prop="invoiceNo" label="发票号" width="120" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleInvoice(row)">登记发票</el-button>
          <el-button v-if="row.status !== 2" link type="success" @click="handlePayment(row)">收款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <!-- 发票登记对话框 -->
    <el-dialog v-model="invoiceVisible" title="登记发票" width="400px">
      <el-form :model="invoiceForm" ref="invoiceFormRef" label-width="80px">
        <el-form-item label="发票号" prop="invoiceNo" :rules="{ required: true, message: '请输入发票号' }">
          <el-input v-model="invoiceForm.invoiceNo" />
        </el-form-item>
        <el-form-item label="开票日期" prop="invoiceDate">
          <el-date-picker v-model="invoiceForm.invoiceDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="invoiceVisible = false">取消</el-button>
        <el-button type="primary" @click="handleInvoiceSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 收款对话框 -->
    <el-dialog v-model="paymentVisible" title="收款核销" width="400px">
      <el-form :model="paymentForm" ref="paymentFormRef" label-width="80px">
        <el-form-item label="收款金额" prop="amount" :rules="{ required: true, message: '请输入收款金额' }">
          <el-input-number v-model="paymentForm.amount" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="收款方式">
          <el-select v-model="paymentForm.paymentMethod" placeholder="请选择" style="width: 100%">
            <el-option label="银行转账" value="银行转账" />
            <el-option label="现金" value="现金" />
            <el-option label="承兑汇票" value="承兑汇票" />
          </el-select>
        </el-form-item>
        <el-form-item label="收款日期">
          <el-date-picker v-model="paymentForm.paymentDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="paymentForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paymentVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePaymentSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, type FormInstance } from 'element-plus';
import { getArRecordPage, updateInvoice, addPayment } from '@/api/finance/arRecord';
import type { ArRecord } from '@/types/finance';
import { AR_STATUS_MAP } from '@/types/finance';

const loading = ref(false);
const tableData = ref<ArRecord[]>([]);
const invoiceVisible = ref(false);
const paymentVisible = ref(false);
const invoiceFormRef = ref<FormInstance>();
const paymentFormRef = ref<FormInstance>();
const keyword = ref('');
const statusFilter = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const currentArId = ref(0);

const invoiceForm = reactive({ invoiceNo: '', invoiceDate: '' });
const paymentForm = reactive({ amount: 0, paymentMethod: '银行转账', paymentDate: '', remark: '' });

onMounted(() => loadData());

async function loadData() {
  loading.value = true;
  try {
    const res = await getArRecordPage({ keyword: keyword.value, status: statusFilter.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleInvoice(row: ArRecord) {
  currentArId.value = row.id;
  invoiceForm.invoiceNo = row.invoiceNo || '';
  invoiceForm.invoiceDate = row.invoiceDate || '';
  invoiceVisible.value = true;
}

async function handleInvoiceSubmit() {
  if (!invoiceFormRef.value) return;
  await invoiceFormRef.value.validate();
  await updateInvoice(currentArId.value, { ...invoiceForm });
  ElMessage.success('发票登记成功');
  invoiceVisible.value = false;
  loadData();
}

function handlePayment(row: ArRecord) {
  currentArId.value = row.id;
  paymentForm.amount = 0;
  paymentForm.paymentMethod = '银行转账';
  paymentForm.paymentDate = '';
  paymentForm.remark = '';
  paymentVisible.value = true;
}

async function handlePaymentSubmit() {
  if (!paymentFormRef.value) return;
  await paymentFormRef.value.validate();
  await addPayment(currentArId.value, { ...paymentForm });
  ElMessage.success('收款成功');
  paymentVisible.value = false;
  loadData();
}

function arStatusText(status?: number) {
  if (status === undefined) return '';
  return AR_STATUS_MAP[status]?.label || '未知';
}

function arStatusType(status?: number) {
  if (status === undefined) return 'info';
  return (AR_STATUS_MAP[status]?.type || 'info') as 'info' | 'warning' | 'success' | 'danger' | '';
}
</script>

<style scoped>
.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.search-bar { display: flex; align-items: center; margin-bottom: 16px; }
</style>
