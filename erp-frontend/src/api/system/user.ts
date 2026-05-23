import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { PageResult } from '@/types/api';
import type { SysUser, UserFormData, UserQuery } from '@/types/system';

/** 分页查询用户 */
export function getUserPage(params: UserQuery): Promise<Result<PageResult<SysUser>>> {
  return request.get('/system/user/page', { params });
}

/** 获取用户详情 */
export function getUserById(id: number): Promise<Result<SysUser>> {
  return request.get(`/system/user/${id}`);
}

/** 创建用户 */
export function createUser(data: UserFormData): Promise<Result<number>> {
  return request.post('/system/user', data);
}

/** 更新用户 */
export function updateUser(id: number, data: UserFormData): Promise<Result<void>> {
  return request.put(`/system/user/${id}`, data);
}

/** 删除用户 */
export function deleteUser(id: number): Promise<Result<void>> {
  return request.delete(`/system/user/${id}`);
}

/** 重置密码 */
export function resetPassword(id: number): Promise<Result<string>> {
  return request.put(`/system/user/${id}/reset-password`);
}

/** 修改状态 */
export function changeUserStatus(id: number, status: number): Promise<Result<void>> {
  return request.put(`/system/user/${id}/status`, null, { params: { status } });
}

/** 修改密码 */
export function changePassword(id: number, oldPassword: string, newPassword: string): Promise<Result<void>> {
  return request.put(`/system/user/${id}/password`, { oldPassword, newPassword });
}
