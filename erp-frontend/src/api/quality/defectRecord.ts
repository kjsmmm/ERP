import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { DefectRecord, DefectRecordFormData } from '@/types/quality';

/** 不合格品记录分页查询 */
export function getDefectRecordPage(params?: { keyword?: string; handleType?: number; status?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<DefectRecord>>> {
  return request.get('/defect-records', { params });
}

/** 创建不合格品记录 */
export function createDefectRecord(data: DefectRecordFormData): Promise<Result<void>> {
  return request.post('/defect-records', data);
}

/** 提交审批 */
export function submitDefectRecord(id: number): Promise<Result<void>> {
  return request.post(`/defect-records/${id}/submit`);
}
