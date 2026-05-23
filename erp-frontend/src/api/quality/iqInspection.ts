import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { IqInspection, IqInspectionFormData } from '@/types/quality';

/** 来料检验分页查询 */
export function getIqInspectionPage(params?: { keyword?: string; inspectionResult?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<IqInspection>>> {
  return request.get('/iq-inspections', { params });
}

/** 创建来料检验单 */
export function createIqInspection(data: IqInspectionFormData): Promise<Result<void>> {
  return request.post('/iq-inspections', data);
}

/** 提交检验结果 */
export function submitIqInspectionResult(id: number, data: IqInspectionFormData): Promise<Result<void>> {
  return request.post(`/iq-inspections/${id}/submit`, data);
}
