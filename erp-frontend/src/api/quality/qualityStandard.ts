import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { QualityStandard, QualityStandardFormData } from '@/types/quality';

/** 检验标准分页查询 */
export function getQualityStandardPage(params?: { keyword?: string; applicableType?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<QualityStandard>>> {
  return request.get('/quality-standards', { params });
}

/** 创建检验标准 */
export function createQualityStandard(data: QualityStandardFormData): Promise<Result<void>> {
  return request.post('/quality-standards', data);
}

/** 更新检验标准 */
export function updateQualityStandard(id: number, data: QualityStandardFormData): Promise<Result<void>> {
  return request.put(`/quality-standards/${id}`, data);
}

/** 删除检验标准 */
export function deleteQualityStandard(id: number): Promise<Result<void>> {
  return request.delete(`/quality-standards/${id}`);
}
