<template>
  <div v-loading="loading">
    <el-page-header @back="router.back()">
      <template #content>
        <span>{{ product?.productName }}</span>
      </template>
    </el-page-header>

    <el-tabs v-model="activeTab" style="margin-top: 16px;">
      <!-- 基本信息 Tab -->
      <el-tab-pane label="基本信息" name="info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="产品编码">{{ product?.productCode }}</el-descriptions-item>
          <el-descriptions-item label="产品名称">{{ product?.productName }}</el-descriptions-item>
          <el-descriptions-item label="产品类型">
            <el-tag :type="product?.productType === 3 ? 'success' : product?.productType === 2 ? 'warning' : 'info'">
              {{ ['', '原材料', '半成品', '成品'][product?.productType ?? 0] }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="分类">{{ product?.categoryName ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="规格型号">{{ product?.spec ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="单位">{{ product?.unit }}</el-descriptions-item>
          <el-descriptions-item label="重量(kg)">{{ product?.weight ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="标准成本">{{ product?.standardCost ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="标准售价">{{ product?.standardPrice ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="product?.status === 1 ? 'success' : 'danger'">
              {{ product?.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ product?.remark ?? '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <!-- 图片管理 Tab -->
      <el-tab-pane label="图片管理" name="images">
        <div style="margin-bottom: 16px;">
          <el-upload
            :action="''"
            :before-upload="handleBeforeUpload"
            :http-request="handleUpload"
            :show-file-list="false"
            accept="image/*"
          >
            <el-button type="primary" v-permission="'product:edit'">上传图片</el-button>
          </el-upload>
        </div>

        <div class="image-list">
          <div v-for="img in images" :key="img.id" class="image-item">
            <el-image
              :src="img.imageUrl"
              :preview-src-list="images.map(i => i.imageUrl)"
              fit="cover"
              style="width: 150px; height: 150px;"
            />
            <div class="image-actions">
              <el-tag v-if="img.isPrimary === 1" type="success" size="small">主图</el-tag>
              <el-button
                v-else
                link
                type="primary"
                size="small"
                v-permission="'product:edit'"
                @click="handleSetPrimary(img.id)"
              >设为主图</el-button>
              <el-button link type="danger" size="small" v-permission="'product:edit'" @click="handleDeleteImage(img.id)">删除</el-button>
            </div>
          </div>
          <el-empty v-if="images.length === 0" description="暂无图片" />
        </div>
      </el-tab-pane>

      <!-- BOM 管理 Tab -->
      <el-tab-pane label="BOM 管理" name="bom" v-if="product?.productType !== 1">
        <div style="margin-bottom: 16px;">
          <el-button type="primary" v-permission="'product:edit'" @click="handleAddBomItem">添加物料</el-button>
          <el-button type="success" v-permission="'product:edit'" @click="handleSaveBom" :loading="bomSaving">保存BOM</el-button>
        </div>

        <el-table :data="bomItems" stripe>
          <el-table-column prop="materialCode" label="物料编码" width="150" />
          <el-table-column prop="materialName" label="物料名称" min-width="150" />
          <el-table-column label="类型" width="80">
            <template #default="{ row }">
              {{ ['', '原材料', '半成品', '成品'][row.materialType] }}
            </template>
          </el-table-column>
          <el-table-column prop="materialUnit" label="单位" width="60" />
          <el-table-column label="用量" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.quantity" :min="0.0001" :precision="4" size="small" style="width:100%;" />
            </template>
          </el-table-column>
          <el-table-column label="损耗率(%)" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.wasteRate" :min="0" :max="100" :precision="2" size="small" style="width:100%;" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" v-permission="'product:edit'">
            <template #default="{ $index }">
              <el-button link type="danger" @click="bomItems.splice($index, 1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="bomItems.length === 0" description="暂无BOM子项" />
      </el-tab-pane>

      <!-- 工艺路线 Tab -->
      <el-tab-pane label="工艺路线" name="route">
        <div style="margin-bottom: 16px;">
          <el-button type="primary" @click="handleAddRoute">新增路线</el-button>
        </div>

        <el-table :data="routeList" stripe v-loading="routeLoading">
          <el-table-column prop="routeCode" label="路线编码" width="120" />
          <el-table-column prop="routeName" label="路线名称" min-width="150" />
          <el-table-column prop="version" label="版本" width="80" />
          <el-table-column label="默认" width="80">
            <template #default="{ row }">
              <el-tag v-if="row.isDefault === 1" type="success" size="small">默认</el-tag>
              <el-button v-else link type="primary" size="small" @click="handleSetDefault(row.id)">设为默认</el-button>
            </template>
          </el-table-column>
          <el-table-column label="步骤数" width="80">
            <template #default="{ row }">{{ row.steps?.length ?? 0 }}</template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="150">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleEditRoute(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDeleteRoute(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="routeList.length === 0 && !routeLoading" description="暂无工艺路线" />
      </el-tab-pane>
    </el-tabs>

    <!-- 添加BOM物料弹窗 -->
    <el-dialog v-model="bomDialogVisible" title="添加物料" width="500px">
      <el-form :model="bomForm" :rules="bomFormRules" ref="bomFormRef" label-width="80px">
        <el-form-item label="物料" prop="materialId">
          <el-select v-model="bomForm.materialId" filterable placeholder="搜索产品" style="width:100%;">
            <el-option
              v-for="p in materialOptions"
              :key="p.id"
              :label="`${p.productCode} - ${p.productName}`"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="用量" prop="quantity">
          <el-input-number v-model="bomForm.quantity" :min="0.0001" :precision="4" style="width:100%;" />
        </el-form-item>
        <el-form-item label="损耗率(%)">
          <el-input-number v-model="bomForm.wasteRate" :min="0" :max="100" :precision="2" style="width:100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bomDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAddBom">确定</el-button>
      </template>
    </el-dialog>

    <!-- 工艺路线弹窗 -->
    <el-dialog v-model="routeDialogVisible" :title="isEditRoute ? '编辑工艺路线' : '新增工艺路线'" width="700px">
      <el-form :model="routeForm" :rules="routeFormRules" ref="routeFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="路线编码" prop="routeCode">
              <el-input v-model="routeForm.routeCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="路线名称" prop="routeName">
              <el-input v-model="routeForm.routeName" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="设为默认">
          <el-switch v-model="routeForm.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="工序步骤">
          <div style="width: 100%;">
            <el-button type="primary" size="small" @click="addStep" style="margin-bottom: 8px;">添加步骤</el-button>
            <el-table :data="routeForm.steps" stripe size="small">
              <el-table-column label="序号" width="60">
                <template #default="{ $index }">{{ $index + 1 }}</template>
              </el-table-column>
              <el-table-column label="步骤名称" min-width="120">
                <template #default="{ row }">
                  <el-input v-model="row.stepName" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="标准工时(分)" width="120">
                <template #default="{ row }">
                  <el-input-number v-model="row.standardTime" :min="0" :precision="1" size="small" style="width:100%;" />
                </template>
              </el-table-column>
              <el-table-column label="设备类型" width="120">
                <template #default="{ row }">
                  <el-input v-model="row.equipmentType" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="说明" width="150">
                <template #default="{ row }">
                  <el-input v-model="row.description" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="60">
                <template #default="{ $index }">
                  <el-button link type="danger" size="small" @click="routeForm.steps.splice($index, 1)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="routeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoute">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getProductById } from '@/api/product/product';
import { getProductImages, uploadProductImage, setPrimaryImage, deleteProductImage } from '@/api/product/image';
import { getBomByProductId, updateBom } from '@/api/product/bom';
import { getProductPage } from '@/api/product/product';
import { getProcessRoutesByProductId, createProcessRoute, updateProcessRoute, deleteProcessRoute, setDefaultProcessRoute } from '@/api/production/processRoute';
import type { ProductDetail, ProductImage, BomItem, BomItemFormData, Product } from '@/types/product';
import type { ProcessRoute, ProcessRouteFormData } from '@/types/production';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const activeTab = ref('info');
const product = ref<ProductDetail | null>(null);
const images = ref<ProductImage[]>([]);
const bomItems = ref<BomItem[]>([]);
const bomSaving = ref(false);

// BOM添加弹窗
const bomDialogVisible = ref(false);
const bomFormRef = ref<FormInstance>();
const materialOptions = ref<Product[]>([]);
const bomForm = reactive<BomItemFormData>({
  materialId: 0,
  quantity: 1,
  wasteRate: 0,
  sortOrder: 0,
});
const bomFormRules = {
  materialId: [{ required: true, message: '请选择物料', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入用量', trigger: 'blur' }],
};

const productId = Number(route.params.id);

// 工艺路线
const routeLoading = ref(false);
const routeList = ref<ProcessRoute[]>([]);
const routeDialogVisible = ref(false);
const isEditRoute = ref(false);
const routeFormRef = ref<FormInstance>();
const editingRouteId = ref(0);
const routeForm = reactive<ProcessRouteFormData>({
  productId,
  routeCode: '',
  routeName: '',
  isDefault: 0,
  steps: [],
});
const routeFormRules = {
  routeCode: [{ required: true, message: '请输入路线编码', trigger: 'blur' }],
  routeName: [{ required: true, message: '请输入路线名称', trigger: 'blur' }],
};

onMounted(() => {
  loadProduct();
  loadImages();
  loadBom();
  loadRoutes();
});

async function loadProduct() {
  loading.value = true;
  try {
    const res = await getProductById(productId);
    product.value = res.data;
  } finally {
    loading.value = false;
  }
}

async function loadImages() {
  const res = await getProductImages(productId);
  images.value = res.data;
}

async function loadBom() {
  const res = await getBomByProductId(productId);
  bomItems.value = res.data;
}

// 图片上传
function handleBeforeUpload(file: File) {
  const isImage = file.type.startsWith('image/');
  const isLt5M = file.size / 1024 / 1024 < 5;
  if (!isImage) {
    ElMessage.error('只能上传图片文件');
    return false;
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB');
    return false;
  }
  return true;
}

async function handleUpload(options: any) {
  await uploadProductImage(productId, options.file);
  ElMessage.success('上传成功');
  loadImages();
}

async function handleSetPrimary(imageId: number) {
  await setPrimaryImage(imageId);
  ElMessage.success('设置成功');
  loadImages();
}

async function handleDeleteImage(imageId: number) {
  await ElMessageBox.confirm('确认删除该图片？', '提示', { type: 'warning' });
  await deleteProductImage(imageId);
  ElMessage.success('删除成功');
  loadImages();
}

// BOM管理
async function handleAddBomItem() {
  // 加载可选物料（排除当前产品）
  const res = await getProductPage({ pageSize: 1000 });
  materialOptions.value = res.data.records.filter(p => p.id !== productId);
  Object.assign(bomForm, { materialId: 0, quantity: 1, wasteRate: 0, sortOrder: bomItems.value.length });
  bomDialogVisible.value = true;
}

function handleConfirmAddBom() {
  if (!bomFormRef.value) return;
  bomFormRef.value.validate((valid) => {
    if (!valid) return;
    const material = materialOptions.value.find(p => p.id === bomForm.materialId);
    if (material) {
      bomItems.value.push({
        id: 0,
        productId,
        materialId: bomForm.materialId,
        quantity: bomForm.quantity,
        wasteRate: bomForm.wasteRate,
        sortOrder: bomItems.value.length,
        materialName: material.productName,
        materialCode: material.productCode,
        materialUnit: material.unit,
        materialSpec: material.spec,
        materialType: material.productType,
      });
    }
    bomDialogVisible.value = false;
  });
}

async function handleSaveBom() {
  bomSaving.value = true;
  try {
    const items: BomItemFormData[] = bomItems.value.map((item, index) => ({
      materialId: item.materialId,
      quantity: item.quantity,
      wasteRate: item.wasteRate,
      sortOrder: index,
    }));
    await updateBom(productId, items);
    ElMessage.success('BOM保存成功');
    loadBom();
  } finally {
    bomSaving.value = false;
  }
}

// 工艺路线管理
async function loadRoutes() {
  routeLoading.value = true;
  try {
    const res = await getProcessRoutesByProductId(productId);
    routeList.value = res.data;
  } finally {
    routeLoading.value = false;
  }
}

function addStep() {
  routeForm.steps.push({
    stepNo: routeForm.steps.length + 1,
    stepName: '',
    standardTime: 0,
    equipmentType: '',
    description: '',
  });
}

function handleAddRoute() {
  isEditRoute.value = false;
  editingRouteId.value = 0;
  Object.assign(routeForm, { productId, routeCode: '', routeName: '', isDefault: 0, steps: [] });
  routeDialogVisible.value = true;
}

function handleEditRoute(row: ProcessRoute) {
  isEditRoute.value = true;
  editingRouteId.value = row.id;
  Object.assign(routeForm, {
    productId,
    routeCode: row.routeCode,
    routeName: row.routeName,
    isDefault: row.isDefault,
    steps: (row.steps || []).map(s => ({ ...s })),
  });
  routeDialogVisible.value = true;
}

async function handleSaveRoute() {
  if (!routeFormRef.value) return;
  await routeFormRef.value.validate();
  // 重新编号步骤
  routeForm.steps.forEach((s, i) => { s.stepNo = i + 1; });
  if (isEditRoute.value) {
    await updateProcessRoute(editingRouteId.value, routeForm);
    ElMessage.success('更新成功');
  } else {
    await createProcessRoute(routeForm);
    ElMessage.success('创建成功');
  }
  routeDialogVisible.value = false;
  loadRoutes();
}

async function handleDeleteRoute(row: ProcessRoute) {
  await ElMessageBox.confirm('确认删除该工艺路线？', '提示', { type: 'warning' });
  await deleteProcessRoute(row.id);
  ElMessage.success('删除成功');
  loadRoutes();
}

async function handleSetDefault(id: number) {
  await setDefaultProcessRoute(id);
  ElMessage.success('设置成功');
  loadRoutes();
}
</script>

<style scoped>
.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}
.image-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 8px;
}
.image-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>
