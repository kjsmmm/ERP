<template>
  <div v-loading="loading">
    <el-page-header @back="router.back()">
      <template #content>
        <span>客户定价 - {{ customerName }}</span>
      </template>
    </el-page-header>

    <div style="margin-top: 16px; display: flex; gap: 12px; align-items: center;">
      <el-select v-model="newProductId" filterable placeholder="选择产品" style="width: 300px;">
        <el-option v-for="p in productList" :key="p.id" :label="`${p.productCode} - ${p.productName}`" :value="p.id" />
      </el-select>
      <el-input-number v-model="newPrice" :min="0.01" :precision="2" placeholder="专属价格" style="width: 150px;" />
      <el-input v-model="newRemark" placeholder="备注" style="width: 200px;" />
      <el-button type="primary" @click="handleAdd">添加</el-button>
    </div>

    <el-table :data="priceList" stripe style="margin-top: 16px;">
      <el-table-column label="产品编码" width="120">
        <template #default="{ row }">{{ getProductName(row.productId, 'code') }}</template>
      </el-table-column>
      <el-table-column label="产品名称" min-width="150">
        <template #default="{ row }">{{ getProductName(row.productId, 'name') }}</template>
      </el-table-column>
      <el-table-column label="标准售价" width="120">
        <template #default="{ row }">{{ getStandardPrice(row.productId) }}</template>
      </el-table-column>
      <el-table-column label="客户专属价格" width="120">
        <template #default="{ row }">
          <span style="color: #E6A23C; font-weight: bold;">{{ row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getCustomerPrices, saveCustomerPrice, deleteCustomerPrice } from '@/api/order/customerPrice';
import { getProductPage } from '@/api/product/product';
import { getCustomerById } from '@/api/customer/customer';
import type { CustomerProductPrice } from '@/api/order/customerPrice';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const customerId = Number(route.params.id);
const customerName = ref('');
const priceList = ref<CustomerProductPrice[]>([]);
const productList = ref<any[]>([]);

const newProductId = ref<number | undefined>(undefined);
const newPrice = ref(0);
const newRemark = ref('');

onMounted(async () => {
  loading.value = true;
  try {
    const [cRes, pRes, pricesRes] = await Promise.all([
      getCustomerById(customerId),
      getProductPage({ pageNum: 1, pageSize: 1000 }),
      getCustomerPrices(customerId),
    ]);
    customerName.value = cRes.data.customerName;
    productList.value = pRes.data.records;
    priceList.value = pricesRes.data;
  } finally {
    loading.value = false;
  }
});

function getProductName(productId: number, field: 'code' | 'name') {
  const p = productList.value.find(item => item.id === productId);
  return field === 'code' ? p?.productCode : p?.productName;
}

function getStandardPrice(productId: number) {
  const p = productList.value.find(item => item.id === productId);
  return p?.standardPrice || '-';
}

async function handleAdd() {
  if (!newProductId.value) {
    ElMessage.error('请选择产品');
    return;
  }
  if (newPrice.value <= 0) {
    ElMessage.error('请输入有效的价格');
    return;
  }
  await saveCustomerPrice(customerId, newProductId.value, newPrice.value, newRemark.value || undefined);
  ElMessage.success('保存成功');
  newProductId.value = undefined;
  newPrice.value = 0;
  newRemark.value = '';
  const res = await getCustomerPrices(customerId);
  priceList.value = res.data;
}

async function handleDelete(row: CustomerProductPrice) {
  await ElMessageBox.confirm('确认删除该客户定价？', '提示', { type: 'warning' });
  await deleteCustomerPrice(row.id);
  ElMessage.success('删除成功');
  const res = await getCustomerPrices(customerId);
  priceList.value = res.data;
}
</script>
