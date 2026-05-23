<template>
  <div>
    <div class="search-area">
      <el-button type="primary" v-permission="'system:permission:add'" @click="handleAdd(0)">新增顶级权限</el-button>
    </div>

    <div class="table-area">
      <el-table :data="treeData" v-loading="loading" row-key="id" default-expand-all>
        <el-table-column prop="permName" label="权限名称" min-width="200" />
        <el-table-column prop="permCode" label="权限编码" width="200" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.permType === 1">目录</el-tag>
            <el-tag type="success" v-else-if="row.permType === 2">菜单</el-tag>
            <el-tag type="warning" v-else>按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="180" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button link type="primary" v-permission="'system:permission:add'" @click="handleAdd(row.id)">新增</el-button>
            <el-button link type="primary" v-permission="'system:permission:edit'" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" v-permission="'system:permission:delete'" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑权限' : '新增权限'" width="550px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="上级权限">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeData"
            :props="{ label: 'permName', value: 'id' }"
            placeholder="无（顶级）"
            check-strictly
            clearable
          />
        </el-form-item>
        <el-form-item label="权限名称" prop="permName">
          <el-input v-model="formData.permName" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permCode">
          <el-input v-model="formData.permCode" />
        </el-form-item>
        <el-form-item label="类型" prop="permType">
          <el-radio-group v-model="formData.permType">
            <el-radio :value="1">目录</el-radio>
            <el-radio :value="2">菜单</el-radio>
            <el-radio :value="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由路径" v-if="formData.permType !== 3">
          <el-input v-model="formData.path" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="formData.permType === 2">
          <el-input v-model="formData.component" />
        </el-form-item>
        <el-form-item label="图标" v-if="formData.permType !== 3">
          <el-input v-model="formData.icon" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" />
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
import { getPermissionTree, createPermission, updatePermission, deletePermission } from '@/api/system/permission';
import type { SysPermission, PermissionFormData } from '@/types/system';

const loading = ref(false);
const treeData = ref<SysPermission[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();

const formData = reactive<PermissionFormData>({
  parentId: 0,
  permName: '',
  permCode: '',
  permType: 1,
  path: '',
  component: '',
  icon: '',
  sortOrder: 0,
  visible: 1,
  remark: '',
});

const formRules = {
  permName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  permType: [{ required: true, message: '请选择类型', trigger: 'change' }],
};

onMounted(() => {
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getPermissionTree();
    treeData.value = res.data;
  } finally {
    loading.value = false;
  }
}

function handleAdd(parentId: number) {
  isEdit.value = false;
  Object.assign(formData, { id: undefined, parentId, permName: '', permCode: '', permType: parentId === 0 ? 1 : 2, path: '', component: '', icon: '', sortOrder: 0, visible: 1, remark: '' });
  dialogVisible.value = true;
}

function handleEdit(row: SysPermission) {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    parentId: row.parentId,
    permName: row.permName,
    permCode: row.permCode,
    permType: row.permType,
    path: row.path,
    component: row.component,
    icon: row.icon,
    sortOrder: row.sortOrder,
    visible: row.visible,
    remark: '',
  });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (formData.id) {
    await updatePermission(formData.id, formData);
    ElMessage.success('更新成功');
  } else {
    await createPermission(formData);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(row: SysPermission) {
  await ElMessageBox.confirm('确认删除该权限？子权限将被一同删除。', '提示', { type: 'warning' });
  await deletePermission(row.id);
  ElMessage.success('删除成功');
  loadData();
}
</script>
