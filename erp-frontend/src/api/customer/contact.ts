import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { CustomerContact, ContactFormData } from '@/types/customer';

/** 获取客户联系人列表 */
export function getContactsByCustomerId(customerId: number): Promise<Result<CustomerContact[]>> {
  return request.get(`/customer/contact/list/${customerId}`);
}

/** 获取联系人详情 */
export function getContactById(id: number): Promise<Result<CustomerContact>> {
  return request.get(`/customer/contact/${id}`);
}

/** 创建联系人 */
export function createContact(data: ContactFormData): Promise<Result<number>> {
  return request.post('/customer/contact', data);
}

/** 更新联系人 */
export function updateContact(id: number, data: ContactFormData): Promise<Result<void>> {
  return request.put(`/customer/contact/${id}`, data);
}

/** 删除联系人 */
export function deleteContact(id: number): Promise<Result<void>> {
  return request.delete(`/customer/contact/${id}`);
}
