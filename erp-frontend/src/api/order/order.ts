import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { SalesOrder, OrderDetail, OrderFormData, OrderQuery } from '@/types/order';

/** 订单分页查询 */
export function getOrderPage(params: OrderQuery): Promise<Result<PageResult<SalesOrder>>> {
  return request.get('/orders', { params });
}

/** 订单详情 */
export function getOrderById(id: number): Promise<Result<OrderDetail>> {
  return request.get(`/orders/${id}`);
}

/** 创建订单 */
export function createOrder(data: OrderFormData): Promise<Result<number>> {
  return request.post('/orders', data);
}

/** 更新订单 */
export function updateOrder(id: number, data: OrderFormData): Promise<Result<void>> {
  return request.put(`/orders/${id}`, data);
}

/** 删除订单 */
export function deleteOrder(id: number): Promise<Result<void>> {
  return request.delete(`/orders/${id}`);
}

/** 确认订单 */
export function confirmOrder(id: number): Promise<Result<void>> {
  return request.put(`/orders/${id}/confirm`);
}

/** 取消订单 */
export function cancelOrder(id: number): Promise<Result<void>> {
  return request.put(`/orders/${id}/cancel`);
}

/** 完成订单 */
export function completeOrder(id: number): Promise<Result<void>> {
  return request.put(`/orders/${id}/complete`);
}

/** 关闭订单 */
export function closeOrder(id: number): Promise<Result<void>> {
  return request.put(`/orders/${id}/close`);
}
