<template>
  <div>
    <h3>入库管理</h3>
    <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px" style="max-width: 600px; margin-top: 16px;">
      <el-form-item label="产品" prop="productId">
        <el-select v-model="formData.productId" filterable placeholder="选择产品" style="width:100%;">
          <el-option v-for="p in productList" :key="p.id" :label="`${p.productCode} - ${p.productName}`" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="formData.warehouseId" placeholder="选择仓库" style="width:100%;">
          <el-option v-for="w in warehouses" :key="w.id" :label="w.warehouseName" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="数量" prop="quantity">
        <el-input-number v-model="formData.quantity" :min="0.0001" :precision="4" style="width:100%;" />
      </el-form-item>
      <el-form-item label="单据号">
        <el-input v-model="formData.referenceNo" placeholder="关联单据号（可选）" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit">确认入库</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, type FormInstance } from 'element-plus';
import { stockIn } from '@/api/inventory/stock';
import { getWarehouseList } from '@/api/inventory/warehouse';
import { getProductPage } from '@/api/product/product';
import type { StockInFormData } from '@/types/inventory';

const formRef = ref<FormInstance>();
const warehouses = ref<any[]>([]);
const productList = ref<any[]>([]);

const formData = reactive<StockInFormData>({
  productId: undefined as any,
  warehouseId: undefined as any,
  quantity: 1,
  referenceNo: '',
  remark: '',
});

const formRules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
};

onMounted(async () => {
  const [wRes, pRes] = await Promise.all([getWarehouseList(), getProductPage({ pageNum: 1, pageSize: 1000 })]);
  warehouses.value = wRes.data;
  productList.value = pRes.data.records;
});

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  await stockIn(formData);
  ElMessage.success('入库成功');
  Object.assign(formData, { productId: undefined, warehouseId: undefined, quantity: 1, referenceNo: '', remark: '' });
}
</script>
