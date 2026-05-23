/** 客户 */
export interface Customer {
  id: number;
  customerCode: string;
  customerName: string;
  customerType: number;
  industry: string;
  customerLevel: number;
  source: string;
  taxNumber: string;
  bankName: string;
  bankAccount: string;
  paymentTerms: string;
  creditLimit: number;
  address: string;
  status: number;
  createdAt: string;
  remark: string;
}

/** 客户详情（含联系人+跟进记录） */
export interface CustomerDetail extends Customer {
  contacts: CustomerContact[];
  recentFollows: CustomerFollow[];
}

/** 客户表单 */
export interface CustomerFormData {
  id?: number;
  customerName: string;
  customerType: number;
  industry: string;
  customerLevel: number;
  source: string;
  taxNumber: string;
  bankName: string;
  bankAccount: string;
  paymentTerms: string;
  creditLimit: number;
  address: string;
  remark: string;
}

/** 客户查询参数 */
export interface CustomerQuery {
  keyword?: string;
  customerType?: number;
  customerLevel?: number;
  industry?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

/** 客户联系人 */
export interface CustomerContact {
  id: number;
  customerId: number;
  contactName: string;
  position: string;
  phone: string;
  email: string;
  isPrimary: number;
  createdAt: string;
}

/** 联系人表单 */
export interface ContactFormData {
  id?: number;
  customerId: number;
  contactName: string;
  position: string;
  phone: string;
  email: string;
  isPrimary: number;
  remark: string;
}

/** 客户跟进记录 */
export interface CustomerFollow {
  id: number;
  customerId: number;
  contactId: number;
  followType: number;
  content: string;
  followTime: string;
  nextFollowTime: string;
  operatorId: number;
  operatorName: string;
  contactName: string;
  createdAt: string;
}

/** 跟进记录表单 */
export interface FollowFormData {
  customerId: number;
  contactId?: number;
  followType: number;
  content: string;
  followTime: string;
  nextFollowTime?: string;
  remark: string;
}

/** 登录请求 */
export interface LoginRequest {
  username: string;
  password: string;
}

/** 登录响应 */
export interface LoginResult {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

/** 用户信息 */
export interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  realName: string;
  email: string;
  phone: string;
  avatar: string;
  roles: string[];
  permissions: string[];
}
