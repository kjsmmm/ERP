import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { ProductCategory, CategoryFormData } from '@/types/product';

/** 获取分类树 */
export function getCategoryTree(): Promise<Result<ProductCategory[]>> {
  return request.get('/product/category/tree');
}

/** 创建分类 */
export function createCategory(data: CategoryFormData): Promise<Result<number>> {
  return request.post('/product/category', data);
}

/** 更新分类 */
export function updateCategory(id: number, data: CategoryFormData): Promise<Result<void>> {
  return request.put(`/product/category/${id}`, data);
}

/** 删除分类 */
export function deleteCategory(id: number): Promise<Result<void>> {
  return request.delete(`/product/category/${id}`);
}
