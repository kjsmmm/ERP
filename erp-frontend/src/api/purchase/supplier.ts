import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { Supplier, SupplierFormData } from '@/types/purchase';

/** 供应商分页查询 */
export function getSupplierPage(params?: { keyword?: string; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<Supplier>>> {
  return request.get('/suppliers', { params });
}

/** 供应商列表（全部启用） */
export function getSupplierList(): Promise<Result<Supplier[]>> {
  return request.get('/suppliers/list');
}

/** 创建供应商 */
export function createSupplier(data: SupplierFormData): Promise<Result<void>> {
  return request.post('/suppliers', data);
}

/** 更新供应商 */
export function updateSupplier(id: number, data: SupplierFormData): Promise<Result<void>> {
  return request.put(`/suppliers/${id}`, data);
}

/** 删除供应商 */
export function deleteSupplier(id: number): Promise<Result<void>> {
  return request.delete(`/suppliers/${id}`);
}
