import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { ProductImage } from '@/types/product';

/** 获取产品图片列表 */
export function getProductImages(productId: number): Promise<Result<ProductImage[]>> {
  return request.get(`/product/${productId}/images`);
}

/** 上传产品图片 */
export function uploadProductImage(productId: number, file: File): Promise<Result<number>> {
  const formData = new FormData();
  formData.append('file', file);
  return request.post(`/product/${productId}/images`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

/** 设为主图 */
export function setPrimaryImage(imageId: number): Promise<Result<void>> {
  return request.put(`/product/image/${imageId}/primary`);
}

/** 删除图片 */
export function deleteProductImage(imageId: number): Promise<Result<void>> {
  return request.delete(`/product/image/${imageId}`);
}
