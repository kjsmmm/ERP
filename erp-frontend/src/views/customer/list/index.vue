<template>
  <div>
    <!-- 搜索区 -->
    <div class="search-area">
      <el-form :inline="true" :model="query">
        <el-form-item label="客户名称/编码">
          <el-input v-model="query.keyword" placeholder="请输入关键字" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.customerType" placeholder="全部" clearable>
            <el-option label="国内" :value="1" />
            <el-option label="国外" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="query.customerLevel" placeholder="全部" clearable>
            <el-option label="A" :value="1" />
            <el-option label="B" :value="2" />
            <el-option label="C" :value="3" />
            <el-option label="D" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="启用" :value="1" />
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
        <h3>客户列表</h3>
        <el-button type="primary" v-permission="'customer:add'" @click="handleAdd">新增客户</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="customerCode" label="客户编码" width="160" />
        <el-table-column prop="customerName" label="客户名称" min-width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="router.push(`/customer/detail/${row.id}`)">{{ row.customerName }}</el-button>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="80">
          <template #default="{ row }">{{ row.customerType === 1 ? '国内' : '国外' }}</template>
        </el-table-column>
        <el-table-column prop="industry" label="行业" width="100" />
        <el-table-column label="等级" width="60">
          <template #default="{ row }">
            <el-tag :type="row.customerLevel === 1 ? 'danger' : row.customerLevel === 2 ? 'warning' : 'info'">
              {{ ['', 'A', 'B', 'C', 'D'][row.customerLevel] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              v-permission="'customer:edit'"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="{ row }">
            <el-button link type="primary" v-permission="'customer:edit'" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="router.push(`/customer/detail/${row.id}`)">详情</el-button>
            <el-button link type="warning" @click="router.push(`/customer/price/${row.id}`)">定价</el-button>
            <el-button link type="danger" v-permission="'customer:delete'" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="700px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerName">
              <el-input v-model="formData.customerName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户类型" prop="customerType">
              <el-select v-model="formData.customerType" style="width:100%;">
                <el-option label="国内" :value="1" />
                <el-option label="国外" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="行业">
              <el-input v-model="formData.industry" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="等级">
              <el-select v-model="formData.customerLevel" style="width:100%;">
                <el-option label="A" :value="1" />
                <el-option label="B" :value="2" />
                <el-option label="C" :value="3" />
                <el-option label="D" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源">
              <el-input v-model="formData.source" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="税号">
              <el-input v-model="formData.taxNumber" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开户行">
              <el-input v-model="formData.bankName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="银行账号">
              <el-input v-model="formData.bankAccount" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账期">
              <el-input v-model="formData.paymentTerms" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信用额度">
              <el-input-number v-model="formData.creditLimit" :min="0" :precision="2" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="地址">
              <el-input v-model="formData.address" />
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
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getCustomerPage, createCustomer, updateCustomer, deleteCustomer, changeCustomerStatus } from '@/api/customer/customer';
import type { Customer, CustomerFormData, CustomerQuery } from '@/types/customer';

const router = useRouter();
const loading = ref(false);
const submitLoading = ref(false);
const tableData = ref<Customer[]>([]);
const total = ref(0);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();

const query = reactive<CustomerQuery>({
  keyword: '',
  customerType: undefined,
  customerLevel: undefined,
  status: undefined,
  pageNum: 1,
  pageSize: 10,
});

const formData = reactive<CustomerFormData>({
  customerName: '',
  customerType: 1,
  industry: '',
  customerLevel: 3,
  source: '',
  taxNumber: '',
  bankName: '',
  bankAccount: '',
  paymentTerms: '',
  creditLimit: 0,
  address: '',
  remark: '',
});

const formRules = {
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  customerType: [{ required: true, message: '请选择客户类型', trigger: 'change' }],
};

onMounted(() => {
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    const res = await getCustomerPage(query);
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
  query.customerType = undefined;
  query.customerLevel = undefined;
  query.status = undefined;
  query.pageNum = 1;
  loadData();
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formData, { id: undefined, customerName: '', customerType: 1, industry: '', customerLevel: 3, source: '', taxNumber: '', bankName: '', bankAccount: '', paymentTerms: '', creditLimit: 0, address: '', remark: '' });
  dialogVisible.value = true;
}

function handleEdit(row: Customer) {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    customerName: row.customerName,
    customerType: row.customerType,
    industry: row.industry,
    customerLevel: row.customerLevel,
    source: row.source,
    taxNumber: row.taxNumber,
    bankName: row.bankName,
    bankAccount: row.bankAccount,
    paymentTerms: row.paymentTerms,
    creditLimit: row.creditLimit,
    address: row.address,
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
      await updateCustomer(formData.id, formData);
      ElMessage.success('更新成功');
    } else {
      await createCustomer(formData);
      ElMessage.success('创建成功');
    }
    dialogVisible.value = false;
    loadData();
  } finally {
    submitLoading.value = false;
  }
}

async function handleDelete(row: Customer) {
  await ElMessageBox.confirm('确认删除该客户？', '提示', { type: 'warning' });
  await deleteCustomer(row.id);
  ElMessage.success('删除成功');
  loadData();
}

async function handleStatusChange(row: Customer) {
  await changeCustomerStatus(row.id, row.status);
  ElMessage.success('状态修改成功');
}
</script>
