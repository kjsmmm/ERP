<template>
  <div v-loading="loading">
    <el-page-header @back="router.push('/customer/list')" style="margin-bottom: 20px;">
      <template #content>
        <span>{{ customer?.customerName }} ({{ customer?.customerCode }})</span>
      </template>
    </el-page-header>

    <el-tabs v-model="activeTab">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="info">
        <el-card>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="客户编码">{{ customer?.customerCode }}</el-descriptions-item>
            <el-descriptions-item label="客户名称">{{ customer?.customerName }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ customer?.customerType === 1 ? '国内' : '国外' }}</el-descriptions-item>
            <el-descriptions-item label="等级">
              <el-tag :type="customer?.customerLevel === 1 ? 'danger' : 'info'">
                {{ ['', 'A', 'B', 'C', 'D'][customer?.customerLevel || 0] }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="行业">{{ customer?.industry || '-' }}</el-descriptions-item>
            <el-descriptions-item label="来源">{{ customer?.source || '-' }}</el-descriptions-item>
            <el-descriptions-item label="税号">{{ customer?.taxNumber || '-' }}</el-descriptions-item>
            <el-descriptions-item label="开户行">{{ customer?.bankName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="银行账号">{{ customer?.bankAccount || '-' }}</el-descriptions-item>
            <el-descriptions-item label="账期">{{ customer?.paymentTerms || '-' }}</el-descriptions-item>
            <el-descriptions-item label="信用额度">{{ customer?.creditLimit || 0 }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="customer?.status === 1 ? 'success' : 'danger'">{{ customer?.status === 1 ? '启用' : '停用' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="地址" :span="2">{{ customer?.address || '-' }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ customer?.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-tab-pane>

      <!-- 联系人 -->
      <el-tab-pane label="联系人" name="contact">
        <el-card>
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center;">
              <span>联系人列表</span>
              <el-button type="primary" size="small" v-permission="'customer:edit'" @click="handleAddContact">新增联系人</el-button>
            </div>
          </template>
          <el-table :data="contacts" stripe>
            <el-table-column prop="contactName" label="姓名" />
            <el-table-column prop="position" label="职位" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column label="主要联系人" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.isPrimary === 1" type="success">是</el-tag>
                <span v-else>否</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button link type="primary" v-permission="'customer:edit'" @click="handleEditContact(row)">编辑</el-button>
                <el-button link type="danger" v-permission="'customer:edit'" @click="handleDeleteContact(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 跟进记录 -->
      <el-tab-pane label="跟进记录" name="follow">
        <el-card>
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center;">
              <span>跟进记录</span>
              <el-button type="primary" size="small" v-permission="'customer:edit'" @click="handleAddFollow">新增跟进</el-button>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="item in follows"
              :key="item.id"
              :timestamp="item.followTime"
              placement="top"
            >
              <el-card shadow="never">
                <div style="display:flex;justify-content:space-between;align-items:center;">
                  <div>
                    <el-tag size="small" style="margin-right:8px;">{{ followTypeMap[item.followType] }}</el-tag>
                    <span style="color:#909399;font-size:12px;">{{ item.operatorName }}</span>
                  </div>
                  <el-button link type="danger" size="small" v-permission="'customer:edit'" @click="handleDeleteFollow(item)">删除</el-button>
                </div>
                <p style="margin-top:8px;">{{ item.content }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-if="follows.length === 0" description="暂无跟进记录" />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 联系人弹窗 -->
    <el-dialog v-model="contactDialogVisible" :title="isEditContact ? '编辑联系人' : '新增联系人'" width="500px">
      <el-form :model="contactForm" :rules="contactRules" ref="contactFormRef" label-width="100px">
        <el-form-item label="姓名" prop="contactName">
          <el-input v-model="contactForm.contactName" />
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="contactForm.position" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="contactForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="contactForm.email" />
        </el-form-item>
        <el-form-item label="主要联系人">
          <el-switch v-model="contactForm.isPrimary" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contactDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitContact">确定</el-button>
      </template>
    </el-dialog>

    <!-- 跟进记录弹窗 -->
    <el-dialog v-model="followDialogVisible" title="新增跟进记录" width="500px">
      <el-form :model="followForm" :rules="followRules" ref="followFormRef" label-width="100px">
        <el-form-item label="跟进类型" prop="followType">
          <el-select v-model="followForm.followType" style="width:100%;">
            <el-option label="电话" :value="1" />
            <el-option label="拜访" :value="2" />
            <el-option label="邮件" :value="3" />
            <el-option label="微信" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人">
          <el-select v-model="followForm.contactId" placeholder="选择联系人（可选）" clearable style="width:100%;">
            <el-option v-for="c in contacts" :key="c.id" :label="c.contactName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容" prop="content">
          <el-input v-model="followForm.content" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="跟进时间" prop="followTime">
          <el-date-picker v-model="followForm.followTime" type="datetime" style="width:100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitFollow">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { getCustomerById } from '@/api/customer/customer';
import { getContactsByCustomerId, createContact, updateContact, deleteContact } from '@/api/customer/contact';
import { getFollowPage, createFollow, deleteFollow } from '@/api/customer/follow';
import type { CustomerDetail, CustomerContact, CustomerFollow, ContactFormData, FollowFormData } from '@/types/customer';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const activeTab = ref('info');
const customer = ref<CustomerDetail | null>(null);
const contacts = ref<CustomerContact[]>([]);
const follows = ref<CustomerFollow[]>([]);

const followTypeMap: Record<number, string> = { 1: '电话', 2: '拜访', 3: '邮件', 4: '微信' };

const customerId = Number(route.params.id);

// 联系人表单
const contactDialogVisible = ref(false);
const isEditContact = ref(false);
const contactFormRef = ref<FormInstance>();
const contactForm = reactive<ContactFormData>({
  customerId,
  contactName: '',
  position: '',
  phone: '',
  email: '',
  isPrimary: 0,
  remark: '',
});
const contactRules = {
  contactName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
};

// 跟进记录表单
const followDialogVisible = ref(false);
const followFormRef = ref<FormInstance>();
const followForm = reactive<FollowFormData>({
  customerId,
  followType: 1,
  content: '',
  followTime: '',
  remark: '',
});
const followRules = {
  followType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  followTime: [{ required: true, message: '请选择时间', trigger: 'change' }],
};

onMounted(async () => {
  loading.value = true;
  try {
    const res = await getCustomerById(customerId);
    customer.value = res.data;
    contacts.value = res.data.contacts || [];
    follows.value = res.data.recentFollows || [];
  } finally {
    loading.value = false;
  }
});

// 联系人操作
function handleAddContact() {
  isEditContact.value = false;
  Object.assign(contactForm, { id: undefined, customerId, contactName: '', position: '', phone: '', email: '', isPrimary: 0, remark: '' });
  contactDialogVisible.value = true;
}

function handleEditContact(row: CustomerContact) {
  isEditContact.value = true;
  Object.assign(contactForm, { id: row.id, customerId: row.customerId, contactName: row.contactName, position: row.position, phone: row.phone, email: row.email, isPrimary: row.isPrimary, remark: '' });
  contactDialogVisible.value = true;
}

async function handleSubmitContact() {
  if (!contactFormRef.value) return;
  await contactFormRef.value.validate();
  if (isEditContact.value && contactForm.id) {
    await updateContact(contactForm.id, contactForm);
    ElMessage.success('更新成功');
  } else {
    await createContact(contactForm);
    ElMessage.success('创建成功');
  }
  contactDialogVisible.value = false;
  const res = await getContactsByCustomerId(customerId);
  contacts.value = res.data;
}

async function handleDeleteContact(row: CustomerContact) {
  await ElMessageBox.confirm('确认删除该联系人？', '提示', { type: 'warning' });
  await deleteContact(row.id);
  ElMessage.success('删除成功');
  const res = await getContactsByCustomerId(customerId);
  contacts.value = res.data;
}

// 跟进记录操作
function handleAddFollow() {
  Object.assign(followForm, { customerId, followType: 1, content: '', followTime: '', contactId: undefined });
  followDialogVisible.value = true;
}

async function handleSubmitFollow() {
  if (!followFormRef.value) return;
  await followFormRef.value.validate();
  await createFollow(followForm);
  ElMessage.success('创建成功');
  followDialogVisible.value = false;
  const res = await getFollowPage(customerId);
  follows.value = res.data.records;
}

async function handleDeleteFollow(item: CustomerFollow) {
  await ElMessageBox.confirm('确认删除该跟进记录？', '提示', { type: 'warning' });
  await deleteFollow(item.id);
  ElMessage.success('删除成功');
  const res = await getFollowPage(customerId);
  follows.value = res.data.records;
}
</script>
