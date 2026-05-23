<template>
  <div>
    <div class="table-header">
      <h3>供应商管理</h3>
      <el-button type="primary" @click="handleAdd">新增供应商</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索供应商编码/名称/联系人" clearable style="width: 300px" @clear="loadData" @keyup.enter="loadData">
        <template #append>
          <el-button @click="loadData">搜索</el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="supplierCode" label="供应商编码" width="120" />
      <el-table-column prop="supplierName" label="供应商名称" min-width="150" />
      <el-table-column prop="contactName" label="联系人" width="100" />
      <el-table-column prop="phone" label="电话" width="120" />
      <el-table-column prop="address" label="地址" min-width="200" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑供应商' : '新增供应商'" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="供应商编码" prop="supplierCode">
          <el-input v-model="formData.supplierCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="formData.supplierName" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="formData.contactName" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="formData.address" />
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
import { getSupplierPage, createSupplier, updateSupplier, deleteSupplier } from '@/api/purchase/supplier';
import type { Supplier, SupplierFormData } from '@/types/purchase';

const loading = ref(false);
const tableData = ref<Supplier[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const editId = ref(0);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const formData = reactive<SupplierFormData>({
  supplierCode: '',
  supplierName: '',
  contactName: '',
  phone: '',
  address: '',
});

const formRules = {
  supplierCode: [{ required: true, message: '请输入供应商编码', trigger: 'blur' }],
  supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
};

onMounted(() => loadData());

async function loadData() {
  loading.value = true;
  try {
    const res = await getSupplierPage({ keyword: keyword.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { supplierCode: '', supplierName: '', contactName: '', phone: '', address: '' });
  dialogVisible.value = true;
}

function handleEdit(row: Supplier) {
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(formData, { supplierCode: row.supplierCode, supplierName: row.supplierName, contactName: row.contactName, phone: row.phone, address: row.address });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (isEdit.value) {
    await updateSupplier(editId.value, formData);
    ElMessage.success('更新成功');
  } else {
    await createSupplier(formData);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(row: Supplier) {
  await ElMessageBox.confirm('确认删除该供应商？', '提示', { type: 'warning' });
  await deleteSupplier(row.id);
  ElMessage.success('删除成功');
  loadData();
}
</script>
