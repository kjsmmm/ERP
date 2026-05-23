import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { PurchaseOrder, PurchaseOrderFormData } from '@/types/purchase';

/** 采购单分页查询 */
export function getPurchaseOrderPage(params?: { keyword?: string; status?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<PurchaseOrder>>> {
  return request.get('/purchase-orders', { params });
}

/** 创建采购单 */
export function createPurchaseOrder(data: PurchaseOrderFormData): Promise<Result<void>> {
  return request.post('/purchase-orders', data);
}

/** 确认采购单 */
export function confirmPurchaseOrder(id: number): Promise<Result<void>> {
  return request.post(`/purchase-orders/${id}/confirm`);
}

/** 取消采购单 */
export function cancelPurchaseOrder(id: number): Promise<Result<void>> {
  return request.post(`/purchase-orders/${id}/cancel`);
}
