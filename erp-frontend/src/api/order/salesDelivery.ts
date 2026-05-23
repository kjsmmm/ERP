import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { SalesDelivery, SalesDeliveryFormData } from '@/types/order';

export function getDeliveryPage(params?: { keyword?: string; status?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<SalesDelivery>>> {
  return request.get('/sales-deliveries', { params });
}

export function createDelivery(data: SalesDeliveryFormData): Promise<Result<void>> {
  return request.post('/sales-deliveries', data);
}

export function pickDelivery(id: number): Promise<Result<void>> {
  return request.post(`/sales-deliveries/${id}/pick`);
}

export function shipOutDelivery(id: number): Promise<Result<void>> {
  return request.post(`/sales-deliveries/${id}/ship-out`);
}

export function signDelivery(id: number): Promise<Result<void>> {
  return request.post(`/sales-deliveries/${id}/sign`);
}
