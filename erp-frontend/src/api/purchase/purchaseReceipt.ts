import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { PurchaseReceipt, PurchaseReceiptFormData } from '@/types/purchase';

/** 采购入库分页查询 */
export function getPurchaseReceiptPage(params?: { keyword?: string; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<PurchaseReceipt>>> {
  return request.get('/purchase-receipts', { params });
}

/** 创建采购入库 */
export function createPurchaseReceipt(data: PurchaseReceiptFormData): Promise<Result<void>> {
  return request.post('/purchase-receipts', data);
}
