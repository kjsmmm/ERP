import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { ProcessRoute, ProcessRouteFormData } from '@/types/production';

/** 按产品查询工艺路线列表 */
export function getProcessRoutesByProductId(productId: number): Promise<Result<ProcessRoute[]>> {
  return request.get(`/process-routes/product/${productId}`);
}

/** 工艺路线详情 */
export function getProcessRouteDetail(id: number): Promise<Result<ProcessRoute>> {
  return request.get(`/process-routes/${id}`);
}

/** 创建工艺路线 */
export function createProcessRoute(data: ProcessRouteFormData): Promise<Result<void>> {
  return request.post('/process-routes', data);
}

/** 更新工艺路线 */
export function updateProcessRoute(id: number, data: ProcessRouteFormData): Promise<Result<void>> {
  return request.put(`/process-routes/${id}`, data);
}

/** 删除工艺路线 */
export function deleteProcessRoute(id: number): Promise<Result<void>> {
  return request.delete(`/process-routes/${id}`);
}

/** 设为默认工艺路线 */
export function setDefaultProcessRoute(id: number): Promise<Result<void>> {
  return request.put(`/process-routes/${id}/default`);
}
