import request from '@/utils/request';
import type { Result } from '@/types/api';

export interface CustomerProductPrice {
  id: number;
  customerId: number;
  productId: number;
  price: number;
  remark: string;
  createdAt: string;
}

/** 获取客户的产品专属价格列表 */
export function getCustomerPrices(customerId: number): Promise<Result<CustomerProductPrice[]>> {
  return request.get(`/customer-prices/customer/${customerId}`);
}

/** 获取客户某产品的专属价格 */
export function getCustomerPrice(customerId: number, productId: number): Promise<Result<number | null>> {
  return request.get(`/customer-prices/customer/${customerId}/product/${productId}`);
}

/** 保存客户产品价格 */
export function saveCustomerPrice(customerId: number, productId: number, price: number, remark?: string): Promise<Result<void>> {
  return request.post('/customer-prices', null, { params: { customerId, productId, price, remark } });
}

/** 删除客户产品价格 */
export function deleteCustomerPrice(id: number): Promise<Result<void>> {
  return request.delete(`/customer-prices/${id}`);
}
