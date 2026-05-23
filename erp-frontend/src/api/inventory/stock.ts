import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { StockInFormData, StockOutFormData } from '@/types/inventory';

/** 入库 */
export function stockIn(data: StockInFormData): Promise<Result<void>> {
  return request.post('/stock/in', data);
}

/** 出库 */
export function stockOut(data: StockOutFormData): Promise<Result<void>> {
  return request.post('/stock/out', data);
}
