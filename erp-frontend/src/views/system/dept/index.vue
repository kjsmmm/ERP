<template>
  <div>
    <el-row :gutter="16">
      <!-- 左侧部门树 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center;">
              <span>部门列表</span>
              <el-button type="primary" size="small" v-permission="'system:dept:add'" @click="handleAddRoot">新增</el-button>
            </div>
          </template>
          <el-tree
            :data="deptTree"
            :props="{ label: 'deptName', children: 'children' }"
            node-key="id"
            highlight-current
            default-expand-all
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <div style="display:flex;justify-content:space-between;align-items:center;width:100%;">
                <span>{{ node.label }}</span>
                <span>
                  <el-button link type="primary" size="small" v-permission="'system:dept:add'" @click.stop="handleAddChild(data)">+</el-button>
                  <el-button link type="danger" size="small" v-permission="'system:dept:delete'" @click.stop="handleDelete(data)">x</el-button>
                </span>
              </div>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧详情/编辑 -->
      <el-col :span="16">
        <el-card>
          <template #header>{{ currentDept ? '编辑部门' : '新增部门' }}</template>
          <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px" style="max-width:500px;">
            <el-form-item label="上级部门">
              <el-tree-select
                v-model="formData.parentId"
                :data="deptTree"
                :props="{ label: 'deptName', value: 'id' }"
                placeholder="无（顶级部门）"
                check-strictly
                clearable
              />
            </el-form-item>
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="formData.deptName" />
            </el-form-item>
            <el-form-item label="部门编码">
              <el-input v-model="formData.deptCode" />
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="formData.sortOrder" :min="0" />
            </el-form-item>
            <el-form-item label="负责人">
              <el-input v-model="formData.leader" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="formData.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="formData.email" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" v-permission="currentDept ? 'system:dept:edit' : 'system:dept:add'" @click="handleSubmit">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getDeptTree, createDept, updateDept, deleteDept } from '@/api/system/dept';
import type { SysDept, DeptFormData } from '@/types/system';

const deptTree = ref<SysDept[]>([]);
const currentDept = ref<SysDept | null>(null);
const formRef = ref<FormInstance>();

const formData = reactive<DeptFormData>({
  parentId: 0,
  deptName: '',
  deptCode: '',
  sortOrder: 0,
  leader: '',
  phone: '',
  email: '',
  remark: '',
});

const formRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
};

onMounted(() => {
  loadTree();
});

async function loadTree() {
  const res = await getDeptTree();
  deptTree.value = res.data;
}

function handleNodeClick(data: SysDept) {
  currentDept.value = data;
  Object.assign(formData, {
    id: data.id,
    parentId: data.parentId,
    deptName: data.deptName,
    deptCode: data.deptCode,
    sortOrder: data.sortOrder,
    leader: data.leader,
    phone: data.phone,
    email: data.email,
    remark: data.remark,
  });
}

function handleAddRoot() {
  currentDept.value = null;
  Object.assign(formData, { id: undefined, parentId: 0, deptName: '', deptCode: '', sortOrder: 0, leader: '', phone: '', email: '', remark: '' });
}

function handleAddChild(data: SysDept) {
  currentDept.value = null;
  Object.assign(formData, { id: undefined, parentId: data.id, deptName: '', deptCode: '', sortOrder: 0, leader: '', phone: '', email: '', remark: '' });
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (formData.id) {
    await updateDept(formData.id, formData);
    ElMessage.success('更新成功');
  } else {
    await createDept(formData);
    ElMessage.success('创建成功');
  }
  loadTree();
}

async function handleDelete(data: SysDept) {
  await ElMessageBox.confirm('确认删除该部门？', '提示', { type: 'warning' });
  await deleteDept(data.id);
  ElMessage.success('删除成功');
  loadTree();
  if (currentDept.value?.id === data.id) {
    handleAddRoot();
  }
}
</script>
