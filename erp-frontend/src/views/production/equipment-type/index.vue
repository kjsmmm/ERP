<template>
  <div>
    <div class="table-header">
      <h3>设备类型管理</h3>
      <el-button type="primary" @click="handleAdd">新增类型</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="typeCode" label="类型编码" width="150" />
      <el-table-column prop="typeName" label="类型名称" min-width="200" />
      <el-table-column prop="description" label="描述" min-width="300" />
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑设备类型' : '新增设备类型'" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="类型编码" prop="typeCode">
          <el-input v-model="formData.typeCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="formData.typeName" />
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
import { getEquipmentTypeList, createEquipmentType, updateEquipmentType, deleteEquipmentType } from '@/api/production/equipmentType';
import type { EquipmentType, EquipmentTypeFormData } from '@/types/production';

const loading = ref(false);
const tableData = ref<EquipmentType[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const editId = ref(0);

const formData = reactive<EquipmentTypeFormData>({
  typeCode: '',
  typeName: '',
  description: '',
});

const formRules = {
  typeCode: [{ required: true, message: '请输入类型编码', trigger: 'blur' }],
  typeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }],
};

onMounted(() => loadData());

async function loadData() {
  loading.value = true;
  try {
    const res = await getEquipmentTypeList();
    tableData.value = res.data;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { typeCode: '', typeName: '', description: '' });
  dialogVisible.value = true;
}

function handleEdit(row: EquipmentType) {
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(formData, { typeCode: row.typeCode, typeName: row.typeName, description: row.description });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (isEdit.value) {
    await updateEquipmentType(editId.value, formData);
    ElMessage.success('更新成功');
  } else {
    await createEquipmentType(formData);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(row: EquipmentType) {
  await ElMessageBox.confirm('确认删除该设备类型？', '提示', { type: 'warning' });
  await deleteEquipmentType(row.id);
  ElMessage.success('删除成功');
  loadData();
}
</script>
