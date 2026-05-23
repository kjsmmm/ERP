<template>
  <div>
    <div class="search-area">
      <el-button type="primary" v-permission="'system:role:add'" @click="handleAdd">新增角色</el-button>
    </div>

    <div class="table-area">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleCode" label="角色编码" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button link type="primary" v-permission="'system:role:edit'" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" v-permission="'system:role:edit'" @click="handlePermission(row)">权限</el-button>
            <el-button link type="danger" v-permission="'system:role:delete'" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限分配弹窗 -->
    <el-dialog v-model="permDialogVisible" title="分配权限" width="500px">
      <el-tree
        ref="permTreeRef"
        :data="permTree"
        :props="{ label: 'permName', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermIds"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permLoading" @click="handleSavePermission">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getAllRoles, createRole, updateRole, deleteRole, assignPermissions } from '@/api/system/role';
import { getPermissionTree, getPermissionIdsByRoleId } from '@/api/system/permission';
import type { SysRole, RoleFormData, SysPermission } from '@/types/system';

const loading = ref(false);
const submitLoading = ref(false);
const permLoading = ref(false);
const tableData = ref<SysRole[]>([]);
const dialogVisible = ref(false);
const permDialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const permTreeRef = ref();
const permTree = ref<SysPermission[]>([]);
const checkedPermIds = ref<number[]>([]);
const currentRoleId = ref(0);

const formData = reactive<RoleFormData>({
  roleName: '',
  roleCode: '',
  sortOrder: 0,
  status: 1,
  remark: '',
});

const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
};

onMounted(() => {
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getAllRoles();
    tableData.value = res.data;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { id: undefined, roleName: '', roleCode: '', sortOrder: 0, status: 1, remark: '' });
  dialogVisible.value = true;
}

function handleEdit(row: SysRole) {
  isEdit.value = true;
  Object.assign(formData, { id: row.id, roleName: row.roleName, roleCode: row.roleCode, sortOrder: row.sortOrder, status: row.status, remark: row.remark });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  submitLoading.value = true;
  try {
    if (isEdit.value && formData.id) {
      await updateRole(formData.id, formData);
      ElMessage.success('更新成功');
    } else {
      await createRole(formData);
      ElMessage.success('创建成功');
    }
    dialogVisible.value = false;
    loadData();
  } finally {
    submitLoading.value = false;
  }
}

async function handleDelete(row: SysRole) {
  await ElMessageBox.confirm('确认删除该角色？', '提示', { type: 'warning' });
  await deleteRole(row.id);
  ElMessage.success('删除成功');
  loadData();
}

async function handlePermission(row: SysRole) {
  currentRoleId.value = row.id;
  const [treeRes, idsRes] = await Promise.all([getPermissionTree(), getPermissionIdsByRoleId(row.id)]);
  permTree.value = treeRes.data;
  checkedPermIds.value = idsRes.data;
  permDialogVisible.value = true;
}

async function handleSavePermission() {
  permLoading.value = true;
  try {
    const checkedKeys = permTreeRef.value.getCheckedKeys(false);
    const halfCheckedKeys = permTreeRef.value.getHalfCheckedKeys();
    await assignPermissions(currentRoleId.value, [...checkedKeys, ...halfCheckedKeys]);
    ElMessage.success('权限分配成功');
    permDialogVisible.value = false;
  } finally {
    permLoading.value = false;
  }
}
</script>
