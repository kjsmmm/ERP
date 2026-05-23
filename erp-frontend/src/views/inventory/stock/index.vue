<template>
  <div>
    <div class="search-area">
      <el-form :inline="true" :model="query">
        <el-form-item label="产品">
          <el-input v-model="query.keyword" placeholder="产品名称/编码" clearable />
        </el-form-item>
        <el-form-item label="仓库">
          <el-select v-model="query.warehouseId" placeholder="全部" clearable>
            <el-option v-for="w in warehouses" :key="w.id" :label="w.warehouseName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="alertCount > 0" type="danger" @click="showAlerts">
            库存预警 ({{ alertCount }})
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 预警提示 -->
    <el-alert v-if="alertCount > 0 && !showingAlerts" :title="`${alertCount} 个产品库存低于安全库存`"
              type="warning" show-icon :closable="false" style="margin-bottom: 16px;">
      <el-button link type="warning" @click="showAlerts">查看详情</el-button>
    </el-alert>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="productCode" label="产品编码" width="150" />
      <el-table-column prop="productName" label="产品名称" min-width="150" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="warehouseName" label="仓库" width="120" />
      <el-table-column label="实物量" width="100">
        <template #default="{ row }">
          <span :style="{ color: isBelowSafety(row) ? '#F56C6C' : '' }">{{ row.onHandQty }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预留量" width="100">
        <template #default="{ row }">{{ row.reservedQty }}</template>
      </el-table-column>
      <el-table-column label="可用量" width="100">
        <template #default="{ row }">
          <span :style="{ color: row.availableQty <= 0 ? '#F56C6C' : '' }">{{ row.availableQty }}</span>
        </template>
      </el-table-column>
      <el-table-column label="安全库存" width="100">
        <template #default="{ row }">
          <span>{{ row.safetyStock || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag v-if="isBelowSafety(row)" type="danger" size="small">预警</el-tag>
          <el-tag v-else type="success" size="small">正常</el-tag>
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
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { getInventoryPage, getInventoryAlerts } from '@/api/inventory/inventory';
import { getWarehouseList } from '@/api/inventory/warehouse';
import type { InventoryItem, InventoryQuery, Warehouse } from '@/types/inventory';

const loading = ref(false);
const tableData = ref<InventoryItem[]>([]);
const total = ref(0);
const warehouses = ref<Warehouse[]>([]);
const alertCount = ref(0);
const showingAlerts = ref(false);

const query = reactive<InventoryQuery>({
  keyword: '',
  warehouseId: undefined,
  pageNum: 1,
  pageSize: 10,
});

onMounted(async () => {
  const [wRes, alertRes] = await Promise.all([getWarehouseList(), getInventoryAlerts()]);
  warehouses.value = wRes.data;
  alertCount.value = alertRes.data.length;
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getInventoryPage(query);
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function isBelowSafety(row: InventoryItem): boolean {
  return row.safetyStock > 0 && row.onHandQty < row.safetyStock;
}

async function showAlerts() {
  showingAlerts.value = true;
  loading.value = true;
  try {
    const res = await getInventoryAlerts();
    tableData.value = res.data;
    total.value = res.data.length;
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  showingAlerts.value = false;
  query.pageNum = 1;
  loadData();
}

function handleReset() {
  showingAlerts.value = false;
  query.keyword = '';
  query.warehouseId = undefined;
  query.pageNum = 1;
  loadData();
}
</script>
