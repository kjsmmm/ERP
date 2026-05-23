import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { Workshop, WorkshopFormData } from '@/types/production';

/** 车间分页查询 */
export function getWorkshopPage(params?: { keyword?: string; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<Workshop>>> {
  return request.get('/workshops', { params });
}

/** 创建车间 */
export function createWorkshop(data: WorkshopFormData): Promise<Result<void>> {
  return request.post('/workshops', data);
}

/** 更新车间 */
export function updateWorkshop(id: number, data: WorkshopFormData): Promise<Result<void>> {
  return request.put(`/workshops/${id}`, data);
}

/** 删除车间 */
export function deleteWorkshop(id: number): Promise<Result<void>> {
  return request.delete(`/workshops/${id}`);
}
