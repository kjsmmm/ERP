<template>
  <div>
    <!-- 搜索区 -->
    <div class="search-area">
      <el-form :inline="true" :model="query">
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="query.realName" placeholder="请输入真实姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区 -->
    <div class="table-area">
      <div class="table-header">
        <h3>用户管理</h3>
        <el-button type="primary" v-permission="'system:user:add'" @click="handleAdd">新增用户</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              v-permission="'system:user:edit'"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" v-permission="'system:user:edit'" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" v-permission="'system:user:resetpwd'" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button link type="danger" v-permission="'system:user:delete'" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="formData.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" />
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="formData.gender">
            <el-radio :value="0">未知</el-radio>
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="formData.deptId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id' }"
            placeholder="请选择部门"
            check-strictly
            clearable
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="formData.roleIds" multiple placeholder="请选择角色">
            <el-option v-for="role in roleList" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getUserPage, createUser, updateUser, deleteUser, resetPassword, changeUserStatus } from '@/api/system/user';
import { getDeptTree } from '@/api/system/dept';
import { getAllRoles } from '@/api/system/role';
import type { SysUser, UserFormData, SysDept, SysRole } from '@/types/system';

const loading = ref(false);
const submitLoading = ref(false);
const tableData = ref<SysUser[]>([]);
const total = ref(0);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const deptTree = ref<SysDept[]>([]);
const roleList = ref<SysRole[]>([]);

const query = reactive({
  username: '',
  realName: '',
  status: undefined as number | undefined,
  pageNum: 1,
  pageSize: 10,
});

const formData = reactive<UserFormData>({
  username: '',
  password: '',
  nickname: '',
  realName: '',
  email: '',
  phone: '',
  gender: 0,
  deptId: null,
  roleIds: [],
  remark: '',
});

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
};

onMounted(() => {
  loadData();
  loadDeptTree();
  loadRoles();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getUserPage(query);
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

async function loadDeptTree() {
  const res = await getDeptTree();
  deptTree.value = res.data;
}

async function loadRoles() {
  const res = await getAllRoles();
  roleList.value = res.data;
}

function handleSearch() {
  query.pageNum = 1;
  loadData();
}

function handleReset() {
  query.username = '';
  query.realName = '';
  query.status = undefined;
  query.pageNum = 1;
  loadData();
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { id: undefined, username: '', password: '', nickname: '', realName: '', email: '', phone: '', gender: 0, deptId: null, roleIds: [], remark: '' });
  dialogVisible.value = true;
}

function handleEdit(row: SysUser) {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    username: row.username,
    nickname: row.nickname,
    realName: row.realName,
    email: row.email,
    phone: row.phone,
    gender: row.gender,
    deptId: row.deptId,
    roleIds: row.roles?.map((r) => r.id) || [],
    remark: row.remark,
  });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  submitLoading.value = true;
  try {
    if (isEdit.value && formData.id) {
      await updateUser(formData.id, formData);
      ElMessage.success('更新成功');
    } else {
      await createUser(formData);
      ElMessage.success('创建成功');
    }
    dialogVisible.value = false;
    loadData();
  } finally {
    submitLoading.value = false;
  }
}

async function handleDelete(row: SysUser) {
  await ElMessageBox.confirm('确认删除该用户？', '提示', { type: 'warning' });
  await deleteUser(row.id);
  ElMessage.success('删除成功');
  loadData();
}

async function handleResetPwd(row: SysUser) {
  await ElMessageBox.confirm(`确认重置用户 "${row.username}" 的密码？`, '提示', { type: 'warning' });
  const res = await resetPassword(row.id);
  ElMessageBox.alert(`新密码：${res.data}`, '重置成功', { type: 'success' });
}

async function handleStatusChange(row: SysUser) {
  await changeUserStatus(row.id, row.status);
  ElMessage.success('状态修改成功');
}
</script>
