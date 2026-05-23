import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { CustomerFollow, FollowFormData } from '@/types/customer';

/** 分页查询跟进记录 */
export function getFollowPage(customerId: number, pageNum = 1, pageSize = 10): Promise<Result<PageResult<CustomerFollow>>> {
  return request.get('/customer/follow/page', { params: { customerId, pageNum, pageSize } });
}

/** 创建跟进记录 */
export function createFollow(data: FollowFormData): Promise<Result<number>> {
  return request.post('/customer/follow', data);
}

/** 删除跟进记录 */
export function deleteFollow(id: number): Promise<Result<void>> {
  return request.delete(`/customer/follow/${id}`);
}
