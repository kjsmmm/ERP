import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { Product, ProductDetail, ProductFormData, ProductQuery } from '@/types/product';

/** 分页查询产品 */
export function getProductPage(params: ProductQuery): Promise<Result<PageResult<Product>>> {
  return request.get('/product/page', { params });
}

/** 获取产品详情 */
export function getProductById(id: number): Promise<Result<ProductDetail>> {
  return request.get(`/product/${id}`);
}

/** 创建产品 */
export function createProduct(data: ProductFormData): Promise<Result<number>> {
  return request.post('/product', data);
}

/** 更新产品 */
export function updateProduct(id: number, data: ProductFormData): Promise<Result<void>> {
  return request.put(`/product/${id}`, data);
}

/** 删除产品 */
export function deleteProduct(id: number): Promise<Result<void>> {
  return request.delete(`/product/${id}`);
}

/** 修改产品状态 */
export function changeProductStatus(id: number, status: number): Promise<Result<void>> {
  return request.put(`/product/${id}/status`, null, { params: { status } });
}
