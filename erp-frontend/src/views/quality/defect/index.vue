<template>
  <div>
    <div class="table-header">
      <h3>不合格品处理</h3>
      <el-button type="primary" @click="handleAdd">新增记录</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索记录编号" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
      <el-select v-model="handleTypeFilter" placeholder="处理方式" clearable style="width: 120px; margin-left: 10px" @change="loadData">
        <el-option label="退货" :value="1" />
        <el-option label="返工" :value="2" />
        <el-option label="报废" :value="3" />
        <el-option label="让步接收" :value="4" />
      </el-select>
      <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px; margin-left: 10px" @change="loadData">
        <el-option label="待审批" :value="0" />
        <el-option label="审批中" :value="1" />
        <el-option label="已通过" :value="2" />
        <el-option label="已驳回" :value="3" />
        <el-option label="已处理" :value="4" />
      </el-select>
      <el-button style="margin-left: 10px" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="recordNo" label="记录编号" width="150" />
      <el-table-column label="来源" width="100">
        <template #default="{ row }">
          {{ row.sourceType === 1 ? 'IQC' : 'OQC' }}
        </template>
      </el-table-column>
      <el-table-column prop="productName" label="产品" width="150" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column label="处理方式" width="100">
        <template #default="{ row }">
          {{ handleTypeText(row.handleType) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
          <el-button v-if="row.status === 0" link type="success" @click="handleSubmit(row)">提交审批</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <!-- 新增记录对话框 -->
    <el-dialog v-model="dialogVisible" title="新增不合格品记录" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="来源类型" prop="sourceType">
          <el-select v-model="formData.sourceType" placeholder="请选择">
            <el-option label="IQC来料检验" :value="1" />
            <el-option label="OQC成品检验" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源ID" prop="sourceId">
          <el-input-number v-model="formData.sourceId" :min="1" />
        </el-form-item>
        <el-form-item label="产品" prop="productId">
          <el-input-number v-model="formData.productId" :min="1" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="formData.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="不合格原因">
          <el-input v-model="formData.defectReason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="处理方式" prop="handleType">
          <el-select v-model="formData.handleType" placeholder="请选择">
            <el-option label="退货" :value="1" />
            <el-option label="返工" :value="2" />
            <el-option label="报废" :value="3" />
            <el-option label="让步接收" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input v-model="formData.handleRemark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailVisible" title="不合格品记录详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="记录编号">{{ currentRecord?.recordNo }}</el-descriptions-item>
        <el-descriptions-item label="来源">{{ currentRecord?.sourceType === 1 ? 'IQC' : 'OQC' }}</el-descriptions-item>
        <el-descriptions-item label="产品">{{ currentRecord?.productName }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ currentRecord?.quantity }}</el-descriptions-item>
        <el-descriptions-item label="处理方式">{{ handleTypeText(currentRecord?.handleType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(currentRecord?.status)">{{ statusText(currentRecord?.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="不合格原因" :span="2">{{ currentRecord?.defectReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理备注" :span="2">{{ currentRecord?.handleRemark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getDefectRecordPage, createDefectRecord, submitDefectRecord } from '@/api/quality/defectRecord';
import type { DefectRecord, DefectRecordFormData } from '@/types/quality';

const loading = ref(false);
const tableData = ref<DefectRecord[]>([]);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const formRef = ref<FormInstance>();
const keyword = ref('');
const handleTypeFilter = ref<number | undefined>(undefined);
const statusFilter = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const currentRecord = ref<DefectRecord | null>(null);

const formData = reactive<DefectRecordFormData>({
  sourceType: 1,
  sourceId: 0,
  productId: 0,
  quantity: 0,
  defectReason: '',
  handleType: 1,
  handleRemark: '',
});

const formRules = {
  sourceType: [{ required: true, message: '请选择来源类型', trigger: 'change' }],
  sourceId: [{ required: true, message: '请输入来源ID', trigger: 'blur' }],
  productId: [{ required: true, message: '请输入产品ID', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  handleType: [{ required: true, message: '请选择处理方式', trigger: 'change' }],
};

onMounted(() => loadData());

async function loadData() {
  loading.value = true;
  try {
    const res = await getDefectRecordPage({ keyword: keyword.value, handleType: handleTypeFilter.value, status: statusFilter.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  Object.assign(formData, { sourceType: 1, sourceId: 0, productId: 0, quantity: 0, defectReason: '', handleType: 1, handleRemark: '' });
  dialogVisible.value = true;
}

async function handleCreate() {
  if (!formRef.value) return;
  await formRef.value.validate();
  await createDefectRecord(formData);
  ElMessage.success('创建成功');
  dialogVisible.value = false;
  loadData();
}

async function handleSubmit(row: DefectRecord) {
  await ElMessageBox.confirm('确认提交审批？', '提示', { type: 'warning' });
  await submitDefectRecord(row.id);
  ElMessage.success('已提交审批');
  loadData();
}

function handleView(row: DefectRecord) {
  currentRecord.value = row;
  detailVisible.value = true;
}

function handleTypeText(type?: number) {
  const map: Record<number, string> = { 1: '退货', 2: '返工', 3: '报废', 4: '让步接收' };
  return type !== undefined ? map[type] || '未知' : '';
}

function statusText(status?: number) {
  const map: Record<number, string> = { 0: '待审批', 1: '审批中', 2: '已通过', 3: '已驳回', 4: '已处理' };
  return status !== undefined ? map[status] || '未知' : '';
}

function statusType(status?: number) {
  const map: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: 'success' };
  return status !== undefined ? map[status] || 'info' : 'info';
}
</script>
