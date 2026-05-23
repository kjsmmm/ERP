<template>
  <div>
    <div class="search-area">
      <el-form :inline="true" :model="query">
        <el-form-item label="操作模块">
          <el-input v-model="query.module" placeholder="请输入模块" clearable />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="query.operatorName" placeholder="请输入操作人" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-area">
      <div class="table-header">
        <h3>操作日志</h3>
        <el-button type="danger" v-permission="'system:log:delete'" @click="handleClear">清空日志</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="module" label="操作模块" width="120" />
        <el-table-column prop="operation" label="操作类型" width="120" />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="operatorIp" label="IP" width="140" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executeTime" label="耗时(ms)" width="100" />
        <el-table-column prop="createdAt" label="操作时间" width="180" />
        <el-table-column label="操作" fixed="right" width="140">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button link type="danger" v-permission="'system:log:delete'" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="操作模块">{{ detailData?.module }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ detailData?.operation }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData?.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="IP">{{ detailData?.operatorIp }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ detailData?.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="请求URL">{{ detailData?.requestUrl }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre style="white-space:pre-wrap;word-break:break-all;">{{ detailData?.requestParams }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应结果" :span="2">
          <pre style="white-space:pre-wrap;word-break:break-all;">{{ detailData?.responseResult }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="detailData?.errorMsg">
          <pre style="color:red;white-space:pre-wrap;">{{ detailData?.errorMsg }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getLogPage, deleteLog, clearLogs } from '@/api/system/log';
import type { SysLog, LogQuery } from '@/types/system';

const loading = ref(false);
const tableData = ref<SysLog[]>([]);
const total = ref(0);
const detailVisible = ref(false);
const detailData = ref<SysLog | null>(null);

const query = reactive<LogQuery>({
  module: '',
  operatorName: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10,
});

onMounted(() => {
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getLogPage(query);
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  query.pageNum = 1;
  loadData();
}

function handleReset() {
  query.module = '';
  query.operatorName = '';
  query.status = undefined;
  query.pageNum = 1;
  loadData();
}

function handleDetail(row: SysLog) {
  detailData.value = row;
  detailVisible.value = true;
}

async function handleDelete(row: SysLog) {
  await ElMessageBox.confirm('确认删除该日志？', '提示', { type: 'warning' });
  await deleteLog(row.id);
  ElMessage.success('删除成功');
  loadData();
}

async function handleClear() {
  await ElMessageBox.confirm('确认清空所有日志？此操作不可恢复。', '警告', { type: 'warning' });
  await clearLogs();
  ElMessage.success('清空成功');
  loadData();
}
</script>
