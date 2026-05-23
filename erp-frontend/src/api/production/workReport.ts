import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { WorkReport, WorkReportFormData } from '@/types/production';

/** 创建报工 */
export function createWorkReport(data: WorkReportFormData): Promise<Result<void>> {
  return request.post('/work-reports', data);
}

/** 按工单查询报工记录 */
export function getWorkReportsByOrderId(workOrderId: number): Promise<Result<WorkReport[]>> {
  return request.get(`/work-reports/order/${workOrderId}`);
}
