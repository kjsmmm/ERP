<template>
  <div>
    <!-- 搜索区 -->
    <div class="search-area">
      <el-form :inline="true" :model="query">
        <el-form-item label="产品名称/编码">
          <el-input v-model="query.keyword" placeholder="请输入关键字" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable>
            <el-option v-for="cat in flatCategories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.productType" placeholder="全部" clearable>
            <el-option label="原材料" :value="1" />
            <el-option label="半成品" :value="2" />
            <el-option label="成品" :value="3" />
          </el-select>
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
        <h3>产品列表</h3>
        <el-button type="primary" v-permission="'product:add'" @click="handleAdd">新增产品</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="productCode" label="产品编码" width="150" />
        <el-table-column prop="productName" label="产品名称" min-width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="router.push(`/product/detail/${row.id}`)">{{ row.productName }}</el-button>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.productType === 3 ? 'success' : row.productType === 2 ? 'warning' : 'info'">
              {{ ['', '原材料', '半成品', '成品'][row.productType] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="spec" label="规格型号" width="120" />
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column label="标准售价" width="100">
          <template #default="{ row }">{{ row.standardPrice ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              v-permission="'product:edit'"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" v-permission="'product:edit'" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="router.push(`/product/detail/${row.id}`)">详情</el-button>
            <el-button link type="danger" v-permission="'product:delete'" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑产品' : '新增产品'" width="700px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="产品编码" prop="productCode">
              <el-input v-model="formData.productCode" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品名称" prop="productName">
              <el-input v-model="formData.productName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品类型" prop="productType">
              <el-select v-model="formData.productType" style="width:100%;">
                <el-option label="原材料" :value="1" />
                <el-option label="半成品" :value="2" />
                <el-option label="成品" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="formData.categoryId" placeholder="请选择" clearable style="width:100%;">
                <el-option v-for="cat in flatCategories" :key="cat.id" :label="cat.name" :value="cat.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号">
              <el-input v-model="formData.spec" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="formData.unit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="重量(kg)">
              <el-input-number v-model="formData.weight" :min="0" :precision="3" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标准成本">
              <el-input-number v-model="formData.standardCost" :min="0" :precision="2" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标准售价">
              <el-input-number v-model="formData.standardPrice" :min="0" :precision="2" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="formData.remark" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getProductPage, createProduct, updateProduct, deleteProduct, changeProductStatus } from '@/api/product/product';
import { getCategoryTree } from '@/api/product/category';
import type { Product, ProductCategory, ProductFormData, ProductQuery } from '@/types/product';

const router = useRouter();
const loading = ref(false);
const submitLoading = ref(false);
const tableData = ref<Product[]>([]);
const total = ref(0);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const categoryTree = ref<ProductCategory[]>([]);
const flatCategories = computed(() => {
  const result: ProductCategory[] = [];
  function flatten(items: ProductCategory[], depth = 0) {
    for (const item of items) {
      result.push({ ...item, name: '  '.repeat(depth) + item.name });
      if (item.children) flatten(item.children, depth + 1);
    }
  }
  flatten(categoryTree.value);
  return result;
});

const query = reactive<ProductQuery>({
  keyword: '',
  categoryId: undefined,
  productType: undefined,
  status: undefined,
  pageNum: 1,
  pageSize: 10,
});

const formData = reactive<ProductFormData>({
  productCode: '',
  productName: '',
  categoryId: undefined as any,
  productType: 1,
  spec: '',
  unit: '',
  weight: undefined as any,
  standardCost: undefined as any,
  standardPrice: undefined as any,
  remark: '',
});

const formRules = {
  productCode: [{ required: true, message: '请输入产品编码', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  productType: [{ required: true, message: '请选择产品类型', trigger: 'change' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
};

onMounted(() => {
  loadData();
  loadCategories();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getProductPage(query);
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

async function loadCategories() {
  const res = await getCategoryTree();
  categoryTree.value = res.data;
}

function handleSearch() {
  query.pageNum = 1;
  loadData();
}

function handleReset() {
  query.keyword = '';
  query.categoryId = undefined;
  query.productType = undefined;
  query.status = undefined;
  query.pageNum = 1;
  loadData();
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, {
    id: undefined, productCode: '', productName: '', categoryId: undefined,
    productType: 1, spec: '', unit: '', weight: undefined,
    standardCost: undefined, standardPrice: undefined, remark: '',
  });
  dialogVisible.value = true;
}

function handleEdit(row: Product) {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    productCode: row.productCode,
    productName: row.productName,
    categoryId: row.categoryId,
    productType: row.productType,
    spec: row.spec,
    unit: row.unit,
    weight: row.weight,
    standardCost: row.standardCost,
    standardPrice: row.standardPrice,
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
      await updateProduct(formData.id, formData);
      ElMessage.success('更新成功');
    } else {
      await createProduct(formData);
      ElMessage.success('创建成功');
    }
    dialogVisible.value = false;
    loadData();
  } finally {
    submitLoading.value = false;
  }
}

async function handleDelete(row: Product) {
  await ElMessageBox.confirm('确认删除该产品？', '提示', { type: 'warning' });
  await deleteProduct(row.id);
  ElMessage.success('删除成功');
  loadData();
}

async function handleStatusChange(row: Product) {
  const oldStatus = row.status === 1 ? 0 : 1;
  try {
    await changeProductStatus(row.id, row.status);
    ElMessage.success('状态修改成功');
  } catch (e) {
    row.status = oldStatus;
  }
}
</script>
