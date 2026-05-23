<template>
  <div>
    <div class="table-header">
      <h3>产品分类管理</h3>
      <el-button type="primary" v-permission="'product:category:add'" @click="handleAdd()">新增分类</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe row-key="id" default-expand-all>
      <el-table-column prop="name" label="分类名称" min-width="200" />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="{ row }">
          <el-button link type="primary" v-permission="'product:category:add'" @click="handleAdd(row.id)">添加子分类</el-button>
          <el-button link type="primary" v-permission="'product:category:edit'" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" v-permission="'product:category:delete'" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="formData.parentId" placeholder="顶级分类" clearable style="width:100%;">
            <el-option label="顶级分类" :value="0" />
            <el-option v-for="cat in flatCategories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width:100%;" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
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
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getCategoryTree, createCategory, updateCategory, deleteCategory } from '@/api/product/category';
import type { ProductCategory, CategoryFormData } from '@/types/product';

const loading = ref(false);
const submitLoading = ref(false);
const tableData = ref<ProductCategory[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();

const formData = reactive<CategoryFormData>({
  name: '',
  parentId: 0,
  sortOrder: 0,
  remark: '',
});

const formRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
};

// 扁平化分类列表（用于父分类选择），编辑时排除自身和后代
const flatCategories = computed(() => {
  const result: ProductCategory[] = [];
  const excludeIds = new Set<number>();
  // 编辑模式下收集自身和后代ID
  if (isEdit.value && formData.id) {
    function collectDescendants(items: ProductCategory[]) {
      for (const item of items) {
        excludeIds.add(item.id);
        if (item.children) collectDescendants(item.children);
      }
    }
    function findNode(items: ProductCategory[]): ProductCategory | null {
      for (const item of items) {
        if (item.id === formData.id) return item;
        if (item.children) {
          const found = findNode(item.children);
          if (found) return found;
        }
      }
      return null;
    }
    const node = findNode(tableData.value);
    if (node) collectDescendants([node]);
  }
  function flatten(items: ProductCategory[]) {
    for (const item of items) {
      if (!excludeIds.has(item.id)) {
        result.push(item);
      }
      if (item.children) flatten(item.children);
    }
  }
  flatten(tableData.value);
  return result;
});

onMounted(() => {
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getCategoryTree();
    tableData.value = buildTree(res.data);
  } finally {
    loading.value = false;
  }
}

function buildTree(items: ProductCategory[]): ProductCategory[] {
  const map = new Map<number, ProductCategory>();
  const roots: ProductCategory[] = [];
  items.forEach(item => map.set(item.id, { ...item, children: [] }));
  items.forEach(item => {
    const node = map.get(item.id)!;
    if (item.parentId === 0) {
      roots.push(node);
    } else {
      const parent = map.get(item.parentId);
      if (parent) {
        parent.children = parent.children || [];
        parent.children.push(node);
      }
    }
  });
  return roots;
}

function handleAdd(parentId?: number) {
  isEdit.value = false;
  Object.assign(formData, { id: undefined, name: '', parentId: parentId || 0, sortOrder: 0, remark: '' });
  dialogVisible.value = true;
}

function handleEdit(row: ProductCategory) {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    parentId: row.parentId,
    sortOrder: row.sortOrder,
    remark: '',
  });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  submitLoading.value = true;
  try {
    if (isEdit.value && formData.id) {
      await updateCategory(formData.id, formData);
      ElMessage.success('更新成功');
    } else {
      await createCategory(formData);
      ElMessage.success('创建成功');
    }
    dialogVisible.value = false;
    loadData();
  } finally {
    submitLoading.value = false;
  }
}

async function handleDelete(row: ProductCategory) {
  await ElMessageBox.confirm('确认删除该分类？', '提示', { type: 'warning' });
  await deleteCategory(row.id);
  ElMessage.success('删除成功');
  loadData();
}
</script>
