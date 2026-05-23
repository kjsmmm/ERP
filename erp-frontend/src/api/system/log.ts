import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { SysLog, LogQuery } from '@/types/system';

/** 分页查询日志 */
export function getLogPage(params: LogQuery): Promise<Result<PageResult<SysLog>>> {
  return request.get('/system/log/page', { params });
}

/** 获取日志详情 */
export function getLogById(id: number): Promise<Result<SysLog>> {
  return request.get(`/system/log/${id}`);
}

/** 删除日志 */
export function deleteLog(id: number): Promise<Result<void>> {
  return request.delete(`/system/log/${id}`);
}

/** 清空日志 */
export function clearLogs(): Promise<Result<void>> {
  return request.delete('/system/log/clear');
}
