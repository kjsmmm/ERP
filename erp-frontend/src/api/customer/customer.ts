import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { Customer, CustomerDetail, CustomerFormData, CustomerQuery } from '@/types/customer';

/** 分页查询客户 */
export function getCustomerPage(params: CustomerQuery): Promise<Result<PageResult<Customer>>> {
  return request.get('/customer/page', { params });
}

/** 获取客户详情 */
export function getCustomerById(id: number): Promise<Result<CustomerDetail>> {
  return request.get(`/customer/${id}`);
}

/** 创建客户 */
export function createCustomer(data: CustomerFormData): Promise<Result<number>> {
  return request.post('/customer', data);
}

/** 更新客户 */
export function updateCustomer(id: number, data: CustomerFormData): Promise<Result<void>> {
  return request.put(`/customer/${id}`, data);
}

/** 删除客户 */
export function deleteCustomer(id: number): Promise<Result<void>> {
  return request.delete(`/customer/${id}`);
}

/** 修改客户状态 */
export function changeCustomerStatus(id: number, status: number): Promise<Result<void>> {
  return request.put(`/customer/${id}/status`, null, { params: { status } });
}
