import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { SysDept, DeptFormData } from '@/types/system';

/** 获取部门树 */
export function getDeptTree(): Promise<Result<SysDept[]>> {
  return request.get('/system/dept/tree');
}

/** 获取部门详情 */
export function getDeptById(id: number): Promise<Result<SysDept>> {
  return request.get(`/system/dept/${id}`);
}

/** 创建部门 */
export function createDept(data: DeptFormData): Promise<Result<number>> {
  return request.post('/system/dept', data);
}

/** 更新部门 */
export function updateDept(id: number, data: DeptFormData): Promise<Result<void>> {
  return request.put(`/system/dept/${id}`, data);
}

/** 删除部门 */
export function deleteDept(id: number): Promise<Result<void>> {
  return request.delete(`/system/dept/${id}`);
}
