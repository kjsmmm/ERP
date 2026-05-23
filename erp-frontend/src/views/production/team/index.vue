<template>
  <div>
    <div class="table-header">
      <h3>班组管理</h3>
      <el-button type="primary" @click="handleAdd">新增班组</el-button>
    </div>

    <div class="search-bar">
      <el-select v-model="filterWorkshopId" placeholder="按车间筛选" clearable style="width: 200px" @change="loadData">
        <el-option v-for="w in workshopList" :key="w.id" :label="w.workshopName" :value="w.id" />
      </el-select>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="teamCode" label="班组编码" width="120" />
      <el-table-column prop="teamName" label="班组名称" min-width="150" />
      <el-table-column prop="workshopName" label="所属车间" width="150" />
      <el-table-column prop="leaderName" label="负责人" width="100" />
      <el-table-column prop="memberCount" label="人数" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑班组' : '新增班组'" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="班组编码" prop="teamCode">
          <el-input v-model="formData.teamCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="班组名称" prop="teamName">
          <el-input v-model="formData.teamName" />
        </el-form-item>
        <el-form-item label="所属车间" prop="workshopId">
          <el-select v-model="formData.workshopId" placeholder="选择车间" style="width: 100%">
            <el-option v-for="w in workshopList" :key="w.id" :label="w.workshopName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="formData.leaderId" placeholder="负责人用户ID" />
        </el-form-item>
        <el-form-item label="人数">
          <el-input-number v-model="formData.memberCount" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getTeamPage, createTeam, updateTeam, deleteTeam } from '@/api/production/team';
import { getWorkshopPage } from '@/api/production/workshop';
import type { Team, TeamFormData, Workshop } from '@/types/production';

const loading = ref(false);
const tableData = ref<Team[]>([]);
const workshopList = ref<Workshop[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const editId = ref(0);
const filterWorkshopId = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const formData = reactive<TeamFormData>({
  teamCode: '',
  teamName: '',
  workshopId: undefined as any,
  leaderId: undefined as any,
  memberCount: 0,
});

const formRules = {
  teamCode: [{ required: true, message: '请输入班组编码', trigger: 'blur' }],
  teamName: [{ required: true, message: '请输入班组名称', trigger: 'blur' }],
  workshopId: [{ required: true, message: '请选择所属车间', trigger: 'change' }],
};

onMounted(async () => {
  const res = await getWorkshopPage({ pageSize: 100 });
  workshopList.value = res.data.records;
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getTeamPage({ workshopId: filterWorkshopId.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { teamCode: '', teamName: '', workshopId: undefined, leaderId: undefined, memberCount: 0 });
  dialogVisible.value = true;
}

function handleEdit(row: Team) {
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(formData, { teamCode: row.teamCode, teamName: row.teamName, workshopId: row.workshopId, leaderId: row.leaderId, memberCount: row.memberCount });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (isEdit.value) {
    await updateTeam(editId.value, formData);
    ElMessage.success('更新成功');
  } else {
    await createTeam(formData);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(row: Team) {
  await ElMessageBox.confirm('确认删除该班组？', '提示', { type: 'warning' });
  await deleteTeam(row.id);
  ElMessage.success('删除成功');
  loadData();
}
</script>
