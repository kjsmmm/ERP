<template>
  <div>
    <div class="table-header">
      <h3>成品检验 (OQC)</h3>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索检验单号" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
      <el-select v-model="resultFilter" placeholder="检验结果" clearable style="width: 120px; margin-left: 10px" @change="loadData">
        <el-option label="待检验" :value="0" />
        <el-option label="合格" :value="1" />
        <el-option label="不合格" :value="2" />
      </el-select>
      <el-button style="margin-left: 10px" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="inspectionNo" label="检验单号" width="150" />
      <el-table-column prop="workOrderId" label="工单ID" width="100" />
      <el-table-column prop="productName" label="产品" width="150" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column label="检验结果" width="100">
        <template #default="{ row }">
          <el-tag :type="resultType(row.inspectionResult)">{{ resultText(row.inspectionResult) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" />
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
          <el-button v-if="row.status !== 2" link type="success" @click="handleInspect(row)">检验</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <!-- 检验对话框 -->
    <el-dialog v-model="inspectDialogVisible" title="填写检验结果" width="700px">
      <el-table :data="inspectItems" border size="small">
        <el-table-column prop="itemName" label="检验项目" width="150" />
        <el-table-column prop="inspectionMethod" label="检验方法" width="120" />
        <el-table-column prop="standardValue" label="标准值" width="120" />
        <el-table-column label="实际值" width="120">
          <template #default="{ row }">
            <el-input v-model="row.actualValue" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="判定" width="100">
          <template #default="{ row }">
            <el-select v-model="row.judgment" size="small">
              <el-option label="合格" :value="1" />
              <el-option label="不合格" :value="2" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="inspectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitResult">提交结果</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailVisible" title="成品检验详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="检验单号">{{ currentInspection?.inspectionNo }}</el-descriptions-item>
        <el-descriptions-item label="工单ID">{{ currentInspection?.workOrderId }}</el-descriptions-item>
        <el-descriptions-item label="产品">{{ currentInspection?.productName }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ currentInspection?.quantity }}</el-descriptions-item>
        <el-descriptions-item label="检验结果">
          <el-tag :type="resultType(currentInspection?.inspectionResult)">{{ resultText(currentInspection?.inspectionResult) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(currentInspection?.status)">{{ statusText(currentInspection?.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentInspection?.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <h4 style="margin: 16px 0 8px">检验项目</h4>
      <el-table :data="currentInspection?.items || []" border size="small">
        <el-table-column prop="itemName" label="检验项目" />
        <el-table-column prop="standardValue" label="标准值" />
        <el-table-column prop="actualValue" label="实际值" />
        <el-table-column label="判定" width="80">
          <template #default="{ row }">
            <el-tag :type="row.judgment === 1 ? 'success' : 'danger'" size="small">{{ row.judgment === 1 ? '合格' : '不合格' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getOqInspectionPage, submitOqInspectionResult } from '@/api/quality/oqInspection';
import type { OqInspection, OqInspectionItem } from '@/types/quality';

const loading = ref(false);
const tableData = ref<OqInspection[]>([]);
const inspectDialogVisible = ref(false);
const detailVisible = ref(false);
const keyword = ref('');
const resultFilter = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const currentInspection = ref<OqInspection | null>(null);
const inspectId = ref(0);
const inspectItems = ref<OqInspectionItem[]>([]);

onMounted(() => loadData());

async function loadData() {
  loading.value = true;
  try {
    const res = await getOqInspectionPage({ keyword: keyword.value, inspectionResult: resultFilter.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleInspect(row: OqInspection) {
  inspectId.value = row.id;
  inspectItems.value = row.items?.map(item => ({ ...item })) || [];
  if (inspectItems.value.length === 0) {
    inspectItems.value = [{ itemName: '', inspectionMethod: '', standardValue: '', actualValue: '', judgment: 1 }];
  }
  inspectDialogVisible.value = true;
}

async function handleSubmitResult() {
  await submitOqInspectionResult(inspectId.value, { workOrderId: 0, remark: '', items: inspectItems.value });
  ElMessage.success('检验结果已提交');
  inspectDialogVisible.value = false;
  loadData();
}

function handleView(row: OqInspection) {
  currentInspection.value = row;
  detailVisible.value = true;
}

function resultText(result?: number) {
  const map: Record<number, string> = { 0: '待检验', 1: '合格', 2: '不合格' };
  return result !== undefined ? map[result] || '未知' : '';
}

function resultType(result?: number) {
  const map: Record<number, string> = { 0: 'info', 1: 'success', 2: 'danger' };
  return result !== undefined ? map[result] || 'info' : 'info';
}

function statusText(status?: number) {
  const map: Record<number, string> = { 0: '待检验', 1: '检验中', 2: '已完成' };
  return status !== undefined ? map[status] || '未知' : '';
}

function statusType(status?: number) {
  const map: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'success' };
  return status !== undefined ? map[status] || 'info' : 'info';
}
</script>
