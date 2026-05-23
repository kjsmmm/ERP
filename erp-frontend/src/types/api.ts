/** API 统一响应类型 */
export interface Result<T = any> {
  code: number;
  message: string;
  data: T;
}

/** 分页请求参数 */
export interface PageQuery {
  pageNum?: number;
  pageSize?: number;
}

/** 分页响应数据 */
export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}
