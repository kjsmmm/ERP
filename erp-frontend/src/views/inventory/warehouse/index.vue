<template>
  <div>
    <div class="table-header">
      <h3>仓库管理</h3>
      <el-button type="primary" @click="handleAdd">新增仓库</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="warehouseCode" label="仓库编码" width="120" />
      <el-table-column prop="warehouseName" label="仓库名称" min-width="150" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑仓库' : '新增仓库'" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="仓库编码" prop="warehouseCode">
          <el-input v-model="formData.warehouseCode" />
        </el-form-item>
        <el-form-item label="仓库名称" prop="warehouseName">
          <el-input v-model="formData.warehouseName" />
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
import { getWarehouseList, createWarehouse, updateWarehouse, deleteWarehouse } from '@/api/inventory/warehouse';
import type { Warehouse, WarehouseFormData } from '@/types/inventory';

const loading = ref(false);
const tableData = ref<Warehouse[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const editId = ref(0);

const formData = reactive<WarehouseFormData>({
  warehouseCode: '',
  warehouseName: '',
  address: '',
  manager: '',
  phone: '',
  remark: '',
});

const formRules = {
  warehouseCode: [{ required: true, message: '请输入仓库编码', trigger: 'blur' }],
  warehouseName: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
};

onMounted(() => loadData());

async function loadData() {
  loading.value = true;
  try {
    const res = await getWarehouseList();
    tableData.value = res.data;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { warehouseCode: '', warehouseName: '', address: '', manager: '', phone: '', remark: '' });
  dialogVisible.value = true;
}

function handleEdit(row: Warehouse) {
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(formData, { warehouseCode: row.warehouseCode, warehouseName: row.warehouseName, address: row.address, manager: row.manager, phone: row.phone, remark: '' });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (isEdit.value) {
    await updateWarehouse(editId.value, formData);
    ElMessage.success('更新成功');
  } else {
    await createWarehouse(formData);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(row: Warehouse) {
  await ElMessageBox.confirm('确认删除该仓库？', '提示', { type: 'warning' });
  await deleteWarehouse(row.id);
  ElMessage.success('删除成功');
  loadData();
}
</script>
