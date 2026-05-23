<template>
  <div>
    <div class="search-area">
      <el-form :inline="true" :model="query">
        <el-form-item label="订单号/客户名">
          <el-input v-model="query.keyword" placeholder="请输入关键字" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option v-for="(v, k) in ORDER_STATUS_MAP" :key="k" :label="v.label" :value="Number(k)" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker v-model="dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-area">
      <div class="table-header">
        <h3>销售订单</h3>
        <el-button type="primary" @click="handleAdd">新建订单</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单编号" width="160" />
        <el-table-column prop="customerName" label="客户" min-width="150" />
        <el-table-column label="总金额" width="120">
          <template #default="{ row }">{{ row.totalAmount?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="deliveryDate" label="交货日期" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="ORDER_STATUS_MAP[row.status]?.type as any">
              {{ ORDER_STATUS_MAP[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="router.push(`/order/detail/${row.id}`)">详情</el-button>
            <el-button link type="primary" v-if="row.status === 1" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" v-if="row.status === 1" @click="handleDelete(row)">删除</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getOrderPage, deleteOrder } from '@/api/order/order';
import { ORDER_STATUS_MAP } from '@/types/order';
import type { SalesOrder, OrderQuery } from '@/types/order';

const router = useRouter();
const loading = ref(false);
const tableData = ref<SalesOrder[]>([]);
const total = ref(0);
const dateRange = ref<string[]>();

const query = reactive<OrderQuery>({
  keyword: '',
  status: undefined,
  startDate: undefined,
  endDate: undefined,
  pageNum: 1,
  pageSize: 10,
});

onMounted(() => loadData());

async function loadData() {
  loading.value = true;
  try {
    query.startDate = dateRange.value?.[0];
    query.endDate = dateRange.value?.[1];
    const res = await getOrderPage(query);
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  query.pageNum = 1;
  loadData();
}

function handleReset() {
  query.keyword = '';
  query.status = undefined;
  dateRange.value = undefined;
  query.pageNum = 1;
  loadData();
}

function handleAdd() {
  router.push('/order/detail/new');
}

function handleEdit(row: SalesOrder) {
  router.push(`/order/detail/${row.id}?edit=1`);
}

async function handleDelete(row: SalesOrder) {
  await ElMessageBox.confirm('确认删除该订单？', '提示', { type: 'warning' });
  await deleteOrder(row.id);
  ElMessage.success('删除成功');
  loadData();
}
</script>
