import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { WorkOrder, WorkOrderFormData } from '@/types/production';

/** 工单分页查询 */
export function getWorkOrderPage(params?: { workshopId?: number; status?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<WorkOrder>>> {
  return request.get('/work-orders', { params });
}

/** 工单详情 */
export function getWorkOrderDetail(id: number): Promise<Result<WorkOrder>> {
  return request.get(`/work-orders/${id}`);
}

/** 创建工单 */
export function createWorkOrder(data: WorkOrderFormData): Promise<Result<void>> {
  return request.post('/work-orders', data);
}

/** 更新工单 */
export function updateWorkOrder(id: number, data: WorkOrderFormData): Promise<Result<void>> {
  return request.put(`/work-orders/${id}`, data);
}

/** 删除工单 */
export function deleteWorkOrder(id: number): Promise<Result<void>> {
  return request.delete(`/work-orders/${id}`);
}

/** 下达工单 */
export function releaseWorkOrder(id: number): Promise<Result<void>> {
  return request.put(`/work-orders/${id}/release`);
}

/** 开始生产 */
export function startWorkOrder(id: number): Promise<Result<void>> {
  return request.put(`/work-orders/${id}/start`);
}

/** 完工 */
export function completeWorkOrder(id: number, actualQty: number): Promise<Result<void>> {
  return request.put(`/work-orders/${id}/complete`, null, { params: { actualQty } });
}

/** 关闭工单 */
export function closeWorkOrder(id: number): Promise<Result<void>> {
  return request.put(`/work-orders/${id}/close`);
}
