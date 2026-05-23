<template>
  <div>
    <div class="table-header">
      <h3>车间管理</h3>
      <el-button type="primary" @click="handleAdd">新增车间</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索车间编码/名称" clearable style="width: 300px" @clear="loadData" @keyup.enter="loadData">
        <template #append>
          <el-button @click="loadData">搜索</el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="workshopCode" label="车间编码" width="120" />
      <el-table-column prop="workshopName" label="车间名称" min-width="150" />
      <el-table-column prop="address" label="地址" min-width="200" />
      <el-table-column prop="manager" label="负责人" width="100" />
      <el-table-column prop="phone" label="电话" width="120" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑车间' : '新增车间'" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="车间编码" prop="workshopCode">
          <el-input v-model="formData.workshopCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="车间名称" prop="workshopName">
          <el-input v-model="formData.workshopName" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="formData.address" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="formData.manager" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
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
import { getWorkshopPage, createWorkshop, updateWorkshop, deleteWorkshop } from '@/api/production/workshop';
import type { Workshop, WorkshopFormData } from '@/types/production';

const loading = ref(false);
const tableData = ref<Workshop[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const editId = ref(0);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const formData = reactive<WorkshopFormData>({
  workshopCode: '',
  workshopName: '',
  address: '',
  manager: '',
  phone: '',
  description: '',
});

const formRules = {
  workshopCode: [{ required: true, message: '请输入车间编码', trigger: 'blur' }],
  workshopName: [{ required: true, message: '请输入车间名称', trigger: 'blur' }],
};

onMounted(() => loadData());

async function loadData() {
  loading.value = true;
  try {
    const res = await getWorkshopPage({ keyword: keyword.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { workshopCode: '', workshopName: '', address: '', manager: '', phone: '', description: '' });
  dialogVisible.value = true;
}

function handleEdit(row: Workshop) {
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(formData, { workshopCode: row.workshopCode, workshopName: row.workshopName, address: row.address, manager: row.manager, phone: row.phone, description: row.description });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (isEdit.value) {
    await updateWorkshop(editId.value, formData);
    ElMessage.success('更新成功');
  } else {
    await createWorkshop(formData);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(row: Workshop) {
  await ElMessageBox.confirm('确认删除该车间？', '提示', { type: 'warning' });
  await deleteWorkshop(row.id);
  ElMessage.success('删除成功');
  loadData();
}
</script>
