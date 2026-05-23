<template>
  <div>
    <div class="table-header">
      <h3>检验标准管理</h3>
      <el-button type="primary" @click="handleAdd">新增标准</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索标准编码/名称" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
      <el-select v-model="applicableTypeFilter" placeholder="适用类型" clearable style="width: 120px; margin-left: 10px" @change="loadData">
        <el-option label="原材料" :value="1" />
        <el-option label="半成品" :value="2" />
        <el-option label="成品" :value="3" />
      </el-select>
      <el-button style="margin-left: 10px" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="standardCode" label="标准编码" width="120" />
      <el-table-column prop="standardName" label="标准名称" min-width="150" />
      <el-table-column label="适用类型" width="100">
        <template #default="{ row }">
          {{ applicableTypeText(row.applicableType) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑检验标准' : '新增检验标准'" width="700px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="标准编码" prop="standardCode">
          <el-input v-model="formData.standardCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="标准名称" prop="standardName">
          <el-input v-model="formData.standardName" />
        </el-form-item>
        <el-form-item label="适用类型" prop="applicableType">
          <el-select v-model="formData.applicableType" placeholder="请选择">
            <el-option label="原材料" :value="1" />
            <el-option label="半成品" :value="2" />
            <el-option label="成品" :value="3" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">检验项目</el-divider>

        <div v-for="(item, index) in formData.items" :key="index" style="display: flex; gap: 10px; margin-bottom: 10px; align-items: flex-start;">
          <el-form-item :prop="`items.${index}.itemName`" :rules="[{ required: true, message: '请输入项目名称', trigger: 'blur' }]" style="margin-bottom: 0;">
            <el-input v-model="item.itemName" placeholder="项目名称" style="width: 150px" />
          </el-form-item>
          <el-form-item style="margin-bottom: 0;">
            <el-input v-model="item.inspectionMethod" placeholder="检验方法" style="width: 120px" />
          </el-form-item>
          <el-form-item style="margin-bottom: 0;">
            <el-input v-model="item.standardValue" placeholder="标准值" style="width: 120px" />
          </el-form-item>
          <el-form-item style="margin-bottom: 0;">
            <el-input v-model="item.judgmentRule" placeholder="判定规则" style="width: 120px" />
          </el-form-item>
          <el-button v-if="formData.items.length > 1" type="danger" link @click="formData.items.splice(index, 1)">删除</el-button>
        </div>
        <el-button type="primary" link @click="formData.items.push({ itemName: '', inspectionMethod: '', standardValue: '', judgmentRule: '' })">+ 添加检验项目</el-button>
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
import { getQualityStandardPage, createQualityStandard, updateQualityStandard, deleteQualityStandard } from '@/api/quality/qualityStandard';
import type { QualityStandard, QualityStandardFormData } from '@/types/quality';

const loading = ref(false);
const tableData = ref<QualityStandard[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const editId = ref(0);
const keyword = ref('');
const applicableTypeFilter = ref<number | undefined>(undefined);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const formData = reactive<QualityStandardFormData>({
  standardCode: '',
  standardName: '',
  applicableType: 1,
  items: [{ itemName: '', inspectionMethod: '', standardValue: '', judgmentRule: '' }],
});

const formRules = {
  standardCode: [{ required: true, message: '请输入标准编码', trigger: 'blur' }],
  standardName: [{ required: true, message: '请输入标准名称', trigger: 'blur' }],
  applicableType: [{ required: true, message: '请选择适用类型', trigger: 'change' }],
};

onMounted(() => loadData());

async function loadData() {
  loading.value = true;
  try {
    const res = await getQualityStandardPage({ keyword: keyword.value, applicableType: applicableTypeFilter.value, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { standardCode: '', standardName: '', applicableType: 1, items: [{ itemName: '', inspectionMethod: '', standardValue: '', judgmentRule: '' }] });
  dialogVisible.value = true;
}

function handleEdit(row: QualityStandard) {
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(formData, {
    standardCode: row.standardCode,
    standardName: row.standardName,
    applicableType: row.applicableType,
    items: row.items?.length ? row.items : [{ itemName: '', inspectionMethod: '', standardValue: '', judgmentRule: '' }],
  });
  dialogVisible.value = true;
}

async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate();
  if (isEdit.value) {
    await updateQualityStandard(editId.value, formData);
    ElMessage.success('更新成功');
  } else {
    await createQualityStandard(formData);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(row: QualityStandard) {
  await ElMessageBox.confirm('确认删除该检验标准？', '提示', { type: 'warning' });
  await deleteQualityStandard(row.id);
  ElMessage.success('删除成功');
  loadData();
}

function applicableTypeText(type: number) {
  const map: Record<number, string> = { 1: '原材料', 2: '半成品', 3: '成品' };
  return map[type] || '未知';
}
</script>
