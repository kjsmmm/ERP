import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { BomItem, BomItemFormData } from '@/types/product';

/** 获取产品BOM */
export function getBomByProductId(productId: number): Promise<Result<BomItem[]>> {
  return request.get(`/product/${productId}/bom`);
}

/** 更新产品BOM */
export function updateBom(productId: number, items: BomItemFormData[]): Promise<Result<void>> {
  return request.put(`/product/${productId}/bom`, items);
}

/** 递归展开BOM树 */
export function expandBomTree(productId: number): Promise<Result<BomItem[]>> {
  return request.get(`/product/${productId}/bom/expand`);
}
