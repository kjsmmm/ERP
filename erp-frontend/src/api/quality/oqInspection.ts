import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { OqInspection, OqInspectionFormData } from '@/types/quality';

/** 成品检验分页查询 */
export function getOqInspectionPage(params?: { keyword?: string; inspectionResult?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<OqInspection>>> {
  return request.get('/oq-inspections', { params });
}

/** 创建成品检验单 */
export function createOqInspection(data: OqInspectionFormData): Promise<Result<void>> {
  return request.post('/oq-inspections', data);
}

/** 提交检验结果 */
export function submitOqInspectionResult(id: number, data: OqInspectionFormData): Promise<Result<void>> {
  return request.post(`/oq-inspections/${id}/submit`, data);
}
