import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { ProductionPlan, ProductionPlanFormData } from '@/types/production';

/** 计划分页查询 */
export function getProductionPlanPage(params?: { status?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<ProductionPlan>>> {
  return request.get('/production-plans', { params });
}

/** 创建计划 */
export function createProductionPlan(data: ProductionPlanFormData): Promise<Result<void>> {
  return request.post('/production-plans', data);
}

/** 更新计划 */
export function updateProductionPlan(id: number, data: ProductionPlanFormData): Promise<Result<void>> {
  return request.put(`/production-plans/${id}`, data);
}

/** 删除计划 */
export function deleteProductionPlan(id: number): Promise<Result<void>> {
  return request.delete(`/production-plans/${id}`);
}

/** 下达计划 */
export function releaseProductionPlan(id: number): Promise<Result<void>> {
  return request.put(`/production-plans/${id}/release`);
}

/** 开始执行 */
export function startProductionPlan(id: number): Promise<Result<void>> {
  return request.put(`/production-plans/${id}/start`);
}

/** 完成计划 */
export function completeProductionPlan(id: number): Promise<Result<void>> {
  return request.put(`/production-plans/${id}/complete`);
}
