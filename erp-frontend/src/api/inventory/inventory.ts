import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { InventoryItem, InventoryQuery } from '@/types/inventory';

/** 库存分页查询 */
export function getInventoryPage(params: InventoryQuery): Promise<Result<PageResult<InventoryItem>>> {
  return request.get('/inventory', { params });
}

/** 按产品查库存 */
export function getInventoryByProduct(productId: number): Promise<Result<InventoryItem[]>> {
  return request.get(`/inventory/product/${productId}`);
}

/** 库存预警查询 */
export function getInventoryAlerts(): Promise<Result<InventoryItem[]>> {
  return request.get('/inventory/alert');
}
