<template>
  <div>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="待办任务" name="todo">
        <el-table :data="todoList" v-loading="loading" stripe>
          <el-table-column prop="taskName" label="任务名称" min-width="150" />
          <el-table-column prop="businessKey" label="业务单号" width="150">
            <template #default="{ row }">
              <el-link type="primary" @click="goToOrder(row.businessKey)">查看订单</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="processDefinitionKey" label="流程类型" width="150" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button link type="success" @click="openApproveDialog(row)">通过</el-button>
              <el-button link type="danger" @click="openRejectDialog(row)">驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="已办任务" name="done">
        <el-table :data="doneList" v-loading="loading" stripe>
          <el-table-column prop="taskName" label="任务名称" min-width="150" />
          <el-table-column prop="businessKey" label="业务单号" width="150" />
          <el-table-column prop="processDefinitionKey" label="流程类型" width="150" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="endTime" label="完成时间" width="180" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 审批对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-form label-width="80px">
        <el-form-item label="任务名称">
          <el-input :model-value="currentTask?.taskName" disabled />
        </el-form-item>
        <el-form-item label="审批意见" :required="isReject">
          <el-input v-model="approvalComment" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button :type="isReject ? 'danger' : 'success'" @click="handleApproval">
          {{ isReject ? '驳回' : '通过' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getTodoTasks, getDoneTasks, approveTask, rejectTask } from '@/api/workflow/workflow';
import type { TaskVO } from '@/api/workflow/workflow';

const router = useRouter();
const activeTab = ref('todo');
const loading = ref(false);
const todoList = ref<TaskVO[]>([]);
const doneList = ref<TaskVO[]>([]);

const dialogVisible = ref(false);
const dialogTitle = ref('');
const isReject = ref(false);
const currentTask = ref<TaskVO | null>(null);
const approvalComment = ref('');

onMounted(() => {
  loadTodoTasks();
});

async function loadTodoTasks() {
  loading.value = true;
  try {
    const res = await getTodoTasks();
    todoList.value = res.data;
  } finally {
    loading.value = false;
  }
}

async function loadDoneTasks() {
  loading.value = true;
  try {
    const res = await getDoneTasks();
    doneList.value = res.data;
  } finally {
    loading.value = false;
  }
}

function goToOrder(businessKey: string) {
  router.push(`/order/detail/${businessKey}`);
}

function openApproveDialog(task: TaskVO) {
  currentTask.value = task;
  isReject.value = false;
  dialogTitle.value = '审批通过';
  approvalComment.value = '';
  dialogVisible.value = true;
}

function openRejectDialog(task: TaskVO) {
  currentTask.value = task;
  isReject.value = true;
  dialogTitle.value = '审批驳回';
  approvalComment.value = '';
  dialogVisible.value = true;
}

async function handleApproval() {
  if (!currentTask.value) return;

  if (isReject.value && !approvalComment.value.trim()) {
    ElMessage.error('驳回时必须填写审批意见');
    return;
  }

  if (isReject.value) {
    await rejectTask(currentTask.value.taskId, approvalComment.value);
    ElMessage.success('已驳回');
  } else {
    await approveTask(currentTask.value.taskId, approvalComment.value || undefined);
    ElMessage.success('已通过');
  }

  dialogVisible.value = false;
  loadTodoTasks();
}
</script>
