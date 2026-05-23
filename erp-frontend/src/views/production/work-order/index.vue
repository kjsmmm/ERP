<template>
  <div>
    <div class="table-header">
      <h3>工单管理</h3>
      <el-button type="primary" @click="handleAdd">新增工单</el-button>
    </div>

    <div class="search-bar">
      <el-select v-model="filterWorkshopId" placeholder="按车间筛选" clearable style="width: 200px" @change="loadData">
        <el-option v-for="w in workshopList" :key="w.id" :label="w.workshopName" :value="w.id" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="按状态筛选" clearable style="width: 150px; margin-left: 10px" @change="loadData">
        <el-option label="已创建" :value="0" />
        <el-option label="已下达" :value="1" />
        <el-option label="生产中" :value="2" />
        <el-option label="已完工" :value="3" />
        <el-option label="已关闭" :value="4" />
      </el-select>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="工单编号" width="120" />
      <el-table-column prop="productName" label="产品" min-width="130" />
      <el-table-column prop="workshopName" label="车间" width="120" />
      <el-table-column prop="routeName" label="工艺路线" width="120" />
      <el-table-column prop="plannedQty" label="计划数量" width="90" />
      <el-table-column prop="actualQty" label="实际数量" width="90">
        <template #default="{ row }">{{ row.actualQty ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="['info', 'warning', 'primary', 'success', 'danger'][row.status]">
            {{ ['已创建', '已下达', '生产中', '已完工', '已关闭'][row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="280">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">详情</el-button>
          <el-button link type="primary" @click="handleReport(row)" v-if="row.status === 2">报工</el-button>
          <el-button link type="primary" @click="handleEdit(row)" v-if="row.status === 0">编辑</el-button>
          <el-button link type="success" @click="handleRelease(row)" v-if="row.status === 0">下达</el-button>
          <el-button link type="primary" @click="handleStart(row)" v-if="row.status === 1">开工</el-button>
          <el-button link type="warning" @click="handleComplete(row)" v-if="row.status === 2">完工</el-button>
          <el-button link type="danger" @click="handleClose(row)" v-if="row.status === 3">关闭</el-button>
          <el-button link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑工单' : '新增工单'" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="工单编号" prop="orderNo">
          <el-input v-model="formData.orderNo" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="产品" prop="productId">
          <el-select v-model="formData.productId" filterable placeholder="选择产品" style="width: 100%" @change="handleProductChange">
            <el-option v-for="p in productList" :key="p.id" :label="`${p.productCode} - ${p.productName}`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="车间" prop="workshopId">
          <el-select v-model="formData.workshopId" placeholder="选择车间" style="width: 100%">
            <el-option v-for="w in workshopList" :key="w.id" :label="w.workshopName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="工艺路线" prop="routeId">
          <el-select v-model="formData.routeId" placeholder="选择工艺路线" style="width: 100%">
            <el-option v-for="r in routeList" :key="r.id" :label="`${r.routeName} (v${r.version})${r.isDefault === 1 ? ' [默认]' : ''}`" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划数量" prop="plannedQty">
          <el-input-number v-model="formData.plannedQty" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="formData.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="formData.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="工单详情" width="700px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="工单编号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="产品">{{ detailData.productName }}</el-descriptions-item>
        <el-descriptions-item label="车间">{{ detailData.workshopName }}</el-descriptions-item>
        <el-descriptions-item label="工艺路线">{{ detailData.routeName }}</el-descriptions-item>
        <el-descriptions-item label="计划数量">{{ detailData.plannedQty }}</el-descriptions-item>
        <el-descriptions-item label="实际数量">{{ detailData.actualQty ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="['info', 'warning', 'primary', 'success', 'danger'][detailData.status]">
            {{ ['已创建', '已下达', '生产中', '已完工', '已关闭'][detailData.status] }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <h4 style="margin: 16px 0 8px;">工序步骤（快照）</h4>
      <el-table :data="detailData?.steps ?? []" stripe size="small">
        <el-table-column prop="stepNo" label="序号" width="60" />
        <el-table-column prop="stepName" label="步骤名称" min-width="120" />
        <el-table-column prop="standardTime" label="标准工时(分)" width="110" />
        <el-table-column prop="equipmentType" label="设备类型" width="120" />
        <el-table-column prop="description" label="说明" min-width="150" />
      </el-table>
      <h4 style="margin: 16px 0 8px;">报工记录</h4>
      <el-table :data="reportList" stripe size="small">
        <el-table-column prop="stepNo" label="工序" width="60" />
        <el-table-column prop="stepName" label="工序名称" width="120" />
        <el-table-column prop="reportQty" label="报工数量" width="100" />
        <el-table-column prop="actualHours" label="工时(分)" width="100" />
        <el-table-column prop="reporterName" label="报工人" width="100" />
        <el-table-column prop="reportTime" label="报工时间" min-width="160" />
      </el-table>
      <el-empty v-if="reportList.length === 0" description="暂无报工记录" />
    </el-dialog>

    <!-- 报工弹窗 -->
    <el-dialog v-model="reportVisible" title="报工" width="500px">
      <el-form :model="reportForm" :rules="reportFormRules" ref="reportFormRef" label-width="100px">
        <el-form-item label="工序" prop="stepNo">
          <el-select v-model="reportForm.stepNo" placeholder="选择工序" style="width: 100%" @change="handleStepChange">
            <el-option v-for="s in reportSteps" :key="s.stepNo" :label="`${s.stepNo}. ${s.stepName}`" :value="s.stepNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="报工数量" prop="reportQty">
          <el-input-number v-model="reportForm.reportQty" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实际工时">
          <el-input-number v-model="reportForm.actualHours" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReport">确定</el-button>
      </template>
    </el-dialog>

    <!-- 完工弹窗 -->
    <el-dialog v-model="completeVisible" title="完工确认" width="400px">
      <el-form label-width="100px">
        <el-form-item label="实际数量">
          <el-input-number v-model="completeActualQty" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmComplete">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getWorkOrderPage, getWorkOrderDetail, createWorkOrder, updateWorkOrder, deleteWorkOrder, releaseWorkOrder, startWorkOrder, completeWorkOrder, closeWorkOrder } from '@/api/production/workOrder';
import { getProcessRoutesByProductId } from '@/api/production/processRoute';
import { getWorkshopPage } from '@/api/production/workshop';
import { getProductPage } from '@/api/product/product';
import { createWorkReport, getWorkReportsByOrderId } from '@/api/production/workReport';
import type { WorkOrder, WorkOrderFormData, Workshop, ProcessRoute, WorkReport, WorkReportFormData, WorkOrderStep } from '@/types/production';
import type { Product } from '@/types/product';

const loading = ref(false);
const tableData = ref<WorkOrder[]>([]);
const workshopList = ref<Workshop[]>([]);
const productList = ref<Product[]>([]);
const routeList = ref<ProcessRoute[]>([]);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const completeVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const editId = ref(0);
const detailData = ref<WorkOrder | null>(null);
const completeId = ref(0);
const completeActualQty = ref(0);
const reportVisible = ref(false);
const reportFormRef = ref<FormInstance>();
const reportSteps = ref<WorkOrderStep[]>([]);
const reportList = ref<WorkReport[]>([]);
const reportOrderId = ref(0);
const reportForm = reactive<WorkReportFormData>({
  workOrderId: 0,
  stepNo: undefined as any,
  stepName: '',
  reportQty: 1,
  actualHours: undefined,
});
const reportFormRules = {
  stepNo: [{ required: true, message: '请选择工序', trigger: 'change' }],
  reportQty: [{ required: true, message: '请输入报工数量', trigger: 'blur' }],
};
const filterWorkshopId = ref<number | undefined>(undefined);
const filterStatus = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const formData = reactive<WorkOrderFormData>({
  orderNo: '',
  productId: undefined as any,
  workshopId: undefined as any,
  routeId: undefined as any,
  plannedQty: 1,
  startDate: '',
  endDate: '',
  remark: '',
});

const formRules = {
  orderNo: [{ required: true, message: '请输入工单编号', trigger: 'blur' }],
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  workshopId: [{ required: true, message: '请选择车间', trigger: 'change' }],
  routeId: [{ required: true, message: '请选择工艺路线', trigger: 'change' }],
  plannedQty: [{ required: true, message: '请输入计划数量', trigger: 'blur' }],
};

onMounted(async () => {
  const [wsRes, prodRes] = await Promise.all([
    getWorkshopPage({ pageSize: 100 }),
    getProductPage({ pageSize: 1000 }),
  ]);
  workshopList.value = wsRes.data.records;
  productList.value = prodRes.data.records;
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getWorkOrderPage({ workshopId: filterWorkshopId.value, status: filterStatus.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

async function handleProductChange(productId: number) {
  if (productId) {
    const res = await getProcessRoutesByProductId(productId);
    routeList.value = res.data;
    // 自动选默认路线
    const defaultRoute = res.data.find(r => r.isDefault === 1);
    if (defaultRoute) formData.routeId = defaultRoute.id;
  } else {
    routeList.value = [];
  }
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { orderNo: '', productId: undefined, workshopId: undefined, routeId: undefined, plannedQty: 1, startDate: '', endDate: '', remark: '' });
  routeList.value = [];
  dialogVisible.value = true;
}

async function handleEdit(row: WorkOrder) {
  isEdit.value = true;
  editId.value = row.id;
  // 加载该产品的工艺路线
  if (row.productId) {
    const res = await getProcessRoutesByProductId(row.productId);
    routeList.value = res.data;
  }
  Object.assign(formData, { orderNo: row.orderNo, productId: row.productId, workshopId: row.workshopId, routeId: row.routeId, plannedQty: row.plannedQty, startDate: row.startDate, endDate: row.endDate, remark: row.remark });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (isEdit.value) {
    await updateWorkOrder(editId.value, formData);
    ElMessage.success('更新成功');
  } else {
    await createWorkOrder(formData);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(row: WorkOrder) {
  await ElMessageBox.confirm('确认删除该工单？', '提示', { type: 'warning' });
  await deleteWorkOrder(row.id);
  ElMessage.success('删除成功');
  loadData();
}

async function handleRelease(row: WorkOrder) {
  await ElMessageBox.confirm('确认下达该工单？', '提示', { type: 'warning' });
  await releaseWorkOrder(row.id);
  ElMessage.success('下达成功');
  loadData();
}

async function handleStart(row: WorkOrder) {
  await startWorkOrder(row.id);
  ElMessage.success('已开始生产');
  loadData();
}

function handleComplete(row: WorkOrder) {
  completeId.value = row.id;
  completeActualQty.value = 0;
  completeVisible.value = true;
}

async function confirmComplete() {
  await completeWorkOrder(completeId.value, completeActualQty.value);
  ElMessage.success('已完工');
  completeVisible.value = false;
  loadData();
}

async function handleClose(row: WorkOrder) {
  await ElMessageBox.confirm('确认关闭该工单？', '提示', { type: 'warning' });
  await closeWorkOrder(row.id);
  ElMessage.success('已关闭');
  loadData();
}

async function handleView(row: WorkOrder) {
  const res = await getWorkOrderDetail(row.id);
  detailData.value = res.data;
  const reportRes = await getWorkReportsByOrderId(row.id);
  reportList.value = reportRes.data;
  detailVisible.value = true;
}

async function handleReport(row: WorkOrder) {
  const res = await getWorkOrderDetail(row.id);
  reportSteps.value = res.data.steps ?? [];
  reportOrderId.value = row.id;
  Object.assign(reportForm, { workOrderId: row.id, stepNo: undefined, stepName: '', reportQty: 1, actualHours: undefined });
  reportVisible.value = true;
}

function handleStepChange(stepNo: number) {
  const step = reportSteps.value.find(s => s.stepNo === stepNo);
  if (step) reportForm.stepName = step.stepName;
}

async function handleSubmitReport() {
  if (!reportFormRef.value) return;
  await reportFormRef.value.validate();
  await createWorkReport(reportForm);
  ElMessage.success('报工成功');
  reportVisible.value = false;
  loadData();
}
</script>
