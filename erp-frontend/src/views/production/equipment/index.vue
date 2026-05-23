<template>
  <div>
    <div class="table-header">
      <h3>设备管理</h3>
      <el-button type="primary" @click="handleAdd">新增设备</el-button>
    </div>

    <div class="search-bar">
      <el-select v-model="filterWorkshopId" placeholder="按车间筛选" clearable style="width: 200px" @change="loadData">
        <el-option v-for="w in workshopList" :key="w.id" :label="w.workshopName" :value="w.id" />
      </el-select>
      <el-select v-model="filterTypeId" placeholder="按类型筛选" clearable style="width: 200px; margin-left: 10px" @change="loadData">
        <el-option v-for="t in typeList" :key="t.id" :label="t.typeName" :value="t.id" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="按状态筛选" clearable style="width: 150px; margin-left: 10px" @change="loadData">
        <el-option label="正常" :value="1" />
        <el-option label="停用" :value="0" />
      </el-select>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="equipmentCode" label="设备编码" width="120" />
      <el-table-column prop="equipmentName" label="设备名称" min-width="150" />
      <el-table-column prop="equipmentTypeName" label="设备类型" width="120" />
      <el-table-column prop="workshopName" label="所在车间" width="120" />
      <el-table-column prop="purchaseDate" label="购入日期" width="110" />
      <el-table-column prop="nextMaintenanceDate" label="下次保养" width="110" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end;" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑设备' : '新增设备'" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="设备编码" prop="equipmentCode">
          <el-input v-model="formData.equipmentCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="设备名称" prop="equipmentName">
          <el-input v-model="formData.equipmentName" />
        </el-form-item>
        <el-form-item label="设备类型" prop="equipmentTypeId">
          <el-select v-model="formData.equipmentTypeId" placeholder="选择设备类型" style="width: 100%">
            <el-option v-for="t in typeList" :key="t.id" :label="t.typeName" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所在车间" prop="workshopId">
          <el-select v-model="formData.workshopId" placeholder="选择车间" style="width: 100%">
            <el-option v-for="w in workshopList" :key="w.id" :label="w.workshopName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="购入日期">
          <el-date-picker v-model="formData.purchaseDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="上次保养">
          <el-date-picker v-model="formData.lastMaintenanceDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="下次保养">
          <el-date-picker v-model="formData.nextMaintenanceDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getEquipmentPage, createEquipment, updateEquipment, deleteEquipment } from '@/api/production/equipment';
import { getEquipmentTypeList } from '@/api/production/equipmentType';
import { getWorkshopPage } from '@/api/production/workshop';
import type { Equipment, EquipmentFormData, EquipmentType, Workshop } from '@/types/production';

const loading = ref(false);
const tableData = ref<Equipment[]>([]);
const workshopList = ref<Workshop[]>([]);
const typeList = ref<EquipmentType[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const editId = ref(0);
const filterWorkshopId = ref<number | undefined>(undefined);
const filterTypeId = ref<number | undefined>(undefined);
const filterStatus = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const formData = reactive<EquipmentFormData>({
  equipmentCode: '',
  equipmentName: '',
  equipmentTypeId: undefined as any,
  workshopId: undefined as any,
  purchaseDate: '',
  lastMaintenanceDate: '',
  nextMaintenanceDate: '',
});

const formRules = {
  equipmentCode: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  equipmentName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  equipmentTypeId: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
  workshopId: [{ required: true, message: '请选择所在车间', trigger: 'change' }],
};

onMounted(async () => {
  const [wsRes, typeRes] = await Promise.all([
    getWorkshopPage({ pageSize: 100 }),
    getEquipmentTypeList(),
  ]);
  workshopList.value = wsRes.data.records;
  typeList.value = typeRes.data;
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getEquipmentPage({
      workshopId: filterWorkshopId.value,
      equipmentTypeId: filterTypeId.value,
      status: filterStatus.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { equipmentCode: '', equipmentName: '', equipmentTypeId: undefined, workshopId: undefined, purchaseDate: '', lastMaintenanceDate: '', nextMaintenanceDate: '' });
  dialogVisible.value = true;
}

function handleEdit(row: Equipment) {
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(formData, {
    equipmentCode: row.equipmentCode,
    equipmentName: row.equipmentName,
    equipmentTypeId: row.equipmentTypeId,
    workshopId: row.workshopId,
    purchaseDate: row.purchaseDate,
    lastMaintenanceDate: row.lastMaintenanceDate,
    nextMaintenanceDate: row.nextMaintenanceDate,
  });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (isEdit.value) {
    await updateEquipment(editId.value, formData);
    ElMessage.success('更新成功');
  } else {
    await createEquipment(formData);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(row: Equipment) {
  await ElMessageBox.confirm('确认删除该设备？', '提示', { type: 'warning' });
  await deleteEquipment(row.id);
  ElMessage.success('删除成功');
  loadData();
}
</script>
