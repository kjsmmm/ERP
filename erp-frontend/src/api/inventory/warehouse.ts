import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { Warehouse, WarehouseFormData } from '@/types/inventory';

/** 仓库列表 */
export function getWarehouseList(): Promise<Result<Warehouse[]>> {
  return request.get('/warehouses');
}

/** 创建仓库 */
export function createWarehouse(data: WarehouseFormData): Promise<Result<number>> {
  return request.post('/warehouses', data);
}

/** 更新仓库 */
export function updateWarehouse(id: number, data: WarehouseFormData): Promise<Result<void>> {
  return request.put(`/warehouses/${id}`, data);
}

/** 删除仓库 */
export function deleteWarehouse(id: number): Promise<Result<void>> {
  return request.delete(`/warehouses/${id}`);
}
