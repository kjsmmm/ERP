import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { PurchaseRequest, PurchaseRequestFormData } from '@/types/purchase';

/** 采购申请分页查询 */
export function getPurchaseRequestPage(params?: { keyword?: string; status?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<PurchaseRequest>>> {
  return request.get('/purchase-requests', { params });
}

/** 创建采购申请 */
export function createPurchaseRequest(data: PurchaseRequestFormData): Promise<Result<void>> {
  return request.post('/purchase-requests', data);
}

/** 提交审批 */
export function submitPurchaseRequest(id: number): Promise<Result<void>> {
  return request.post(`/purchase-requests/${id}/submit`);
}
