import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { SalesReturn, SalesReturnFormData } from '@/types/order';

export function getReturnPage(params?: { keyword?: string; status?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<SalesReturn>>> {
  return request.get('/sales-returns', { params });
}

export function createReturn(data: SalesReturnFormData): Promise<Result<void>> {
  return request.post('/sales-returns', data);
}

export function submitReturn(id: number): Promise<Result<void>> {
  return request.post(`/sales-returns/${id}/submit`);
}

export function receiveReturn(id: number): Promise<Result<void>> {
  return request.post(`/sales-returns/${id}/receive`);
}
