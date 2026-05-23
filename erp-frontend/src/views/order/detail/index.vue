<template>
  <div v-loading="loading">
    <el-page-header @back="router.back()">
      <template #content>
        <span>{{ isCreate ? '新建订单' : `订单 ${order?.orderNo}` }}</span>
      </template>
    </el-page-header>

    <!-- 审批状态提示 -->
    <el-alert v-if="order?.status === 8" title="订单变更审批中" type="warning" :closable="false" style="margin-top: 16px;">
      <template #default>
        <span>该订单正在等待审批，审批通过后变更将生效，驳回后变更将丢弃。</span>
        <span v-if="order?.approvalStatus"> 当前状态：{{ order.approvalStatus === 'pending' ? '待审批' : '已审批' }}</span>
      </template>
    </el-alert>

    <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px" style="margin-top: 16px;">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="客户" prop="customerId">
            <el-select v-model="formData.customerId" filterable placeholder="请选择客户" style="width:100%;" :disabled="!canEdit">
              <el-option v-for="c in customerList" :key="c.id" :label="`${c.customerCode} - ${c.customerName}`" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="交货日期" prop="deliveryDate">
            <el-date-picker v-model="formData.deliveryDate" type="date" value-format="YYYY-MM-DD" style="width:100%;" :disabled="!canEdit" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收货地址">
            <el-input v-model="formData.deliveryAddress" :disabled="!canEdit" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话">
            <el-input v-model="formData.contactPhone" :disabled="!canEdit" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="备注">
            <el-input v-model="formData.remark" type="textarea" :rows="2" :disabled="!canEdit" />
          </el-form-item>
        </el-col>
      </el-row>

      <h4>订单明细</h4>
      <el-table :data="formData.items" stripe style="margin-bottom: 16px;">
        <el-table-column label="产品" min-width="200">
          <template #default="{ row, $index }">
            <el-select v-model="row.productId" filterable placeholder="选择产品" style="width:100%;" :disabled="!canEdit" @change="(val: number) => onProductChange(val, $index)">
              <el-option v-for="p in productList" :key="p.id" :label="`${p.productCode} - ${p.productName}`" :value="p.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="0.0001" :precision="4" size="small" style="width:100%;" :disabled="!canEdit" />
          </template>
        </el-table-column>
        <el-table-column label="单价" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.unitPrice" :min="0.01" :precision="2" size="small" style="width:100%;" :disabled="!canEdit" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">{{ (row.quantity * row.unitPrice).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" v-if="canEdit">
          <template #default="{ $index }">
            <el-button link type="danger" @click="formData.items.splice($index, 1)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button v-if="canEdit" type="primary" plain @click="addItem">添加明细</el-button>

      <div style="margin-top: 16px; text-align: right; font-size: 16px;">
        合计金额：<strong>{{ totalAmount.toFixed(2) }}</strong>
      </div>
    </el-form>

    <div style="margin-top: 24px; display: flex; gap: 12px;" v-if="canEdit">
      <el-button type="primary" @click="handleSave">保存</el-button>
      <el-button v-if="order?.status === 1" type="success" @click="handleConfirm">确认订单</el-button>
      <el-button @click="router.back()">取消</el-button>
    </div>

    <div style="margin-top: 24px; display: flex; gap: 12px;" v-if="order && !canEdit">
      <el-button v-if="order.status === 2" type="warning" @click="handleCancel">取消订单</el-button>
      <el-button v-if="order.status === 3" type="success" @click="handleComplete">完成</el-button>
      <el-button v-if="order.status === 4" @click="handleClose">关闭</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, type FormInstance } from 'element-plus';
import { getOrderById, createOrder, updateOrder, confirmOrder, cancelOrder, completeOrder, closeOrder } from '@/api/order/order';
import { getCustomerPage } from '@/api/customer/customer';
import { getProductPage } from '@/api/product/product';
import type { OrderFormData, OrderItemFormData } from '@/types/order';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const formRef = ref<FormInstance>();
const order = ref<any>(null);
const customerList = ref<any[]>([]);
const productList = ref<any[]>([]);

const isCreate = computed(() => route.params.id === 'new');
const canEdit = computed(() => isCreate.value || order.value?.status === 1 || (order.value?.status === 2 && order.value?.approvalStatus !== 'pending'));

const formData = reactive<OrderFormData>({
  customerId: undefined as any,
  deliveryDate: '',
  deliveryAddress: '',
  contactPhone: '',
  remark: '',
  items: [],
});

const formRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  deliveryDate: [{ required: true, message: '请选择交货日期', trigger: 'change' }],
};

const totalAmount = computed(() =>
  formData.items.reduce((sum, item) => sum + item.quantity * item.unitPrice, 0)
);

onMounted(async () => {
  await loadOptions();
  if (!isCreate.value) {
    await loadOrder();
  }
});

async function loadOptions() {
  const [cRes, pRes] = await Promise.all([
    getCustomerPage({ pageNum: 1, pageSize: 1000 }),
    getProductPage({ pageNum: 1, pageSize: 1000 }),
  ]);
  customerList.value = cRes.data.records;
  productList.value = pRes.data.records;
}

async function loadOrder() {
  loading.value = true;
  try {
    const res = await getOrderById(Number(route.params.id));
    order.value = res.data;
    Object.assign(formData, {
      customerId: res.data.customerId,
      deliveryDate: res.data.deliveryDate,
      deliveryAddress: res.data.deliveryAddress,
      contactPhone: res.data.contactPhone,
      remark: res.data.remark,
      items: res.data.items.map((item: any) => ({
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        sortOrder: item.sortOrder,
      })),
    });
  } finally {
    loading.value = false;
  }
}

function addItem() {
  formData.items.push({ productId: 0, quantity: 1, unitPrice: 0, sortOrder: formData.items.length });
}

function onProductChange(productId: number, index: number) {
  const product = productList.value.find(p => p.id === productId);
  if (product) {
    formData.items[index].unitPrice = product.standardPrice || 0;
  }
}

async function handleSave() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (formData.items.length === 0) {
    ElMessage.error('请添加至少一条明细');
    return;
  }
  if (isCreate.value) {
    await createOrder(formData);
    ElMessage.success('创建成功');
  } else {
    await updateOrder(Number(route.params.id), formData);
    ElMessage.success('更新成功');
  }
  router.push('/order/list');
}

async function handleConfirm() {
  await handleSave();
  await confirmOrder(Number(route.params.id));
  ElMessage.success('订单已确认');
  loadOrder();
}

async function handleCancel() {
  await cancelOrder(order.value.id);
  ElMessage.success('订单已取消');
  loadOrder();
}

async function handleComplete() {
  await completeOrder(order.value.id);
  ElMessage.success('订单已完成');
  loadOrder();
}

async function handleClose() {
  await closeOrder(order.value.id);
  ElMessage.success('订单已关闭');
  loadOrder();
}
</script>
