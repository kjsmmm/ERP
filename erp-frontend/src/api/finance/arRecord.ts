import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { ArRecord } from '@/types/finance';

export function getArRecordPage(params?: { keyword?: string; status?: number; customerId?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<ArRecord>>> {
  return request.get('/ar-records', { params });
}

export function updateInvoice(id: number, data: { invoiceNo: string; invoiceDate: string }): Promise<Result<void>> {
  return request.put(`/ar-records/${id}/invoice`, data);
}

export function addPayment(id: number, data: { amount: number; paymentMethod: string; paymentDate: string; remark: string }): Promise<Result<void>> {
  return request.post(`/ar-records/${id}/payment`, data);
}
