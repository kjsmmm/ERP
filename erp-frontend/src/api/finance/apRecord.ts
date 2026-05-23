import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { ApRecord } from '@/types/finance';

export function getApRecordPage(params?: { keyword?: string; status?: number; supplierId?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<ApRecord>>> {
  return request.get('/ap-records', { params });
}

export function updateInvoice(id: number, data: { invoiceNo: string; invoiceDate: string }): Promise<Result<void>> {
  return request.put(`/ap-records/${id}/invoice`, data);
}

export function addPayment(id: number, data: { amount: number; paymentMethod: string; paymentDate: string; remark: string }): Promise<Result<void>> {
  return request.post(`/ap-records/${id}/payment`, data);
}
