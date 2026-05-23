import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { SysPermission, PermissionFormData } from '@/types/system';

/** 获取权限树 */
export function getPermissionTree(): Promise<Result<SysPermission[]>> {
  return request.get('/system/permission/tree');
}

/** 获取权限详情 */
export function getPermissionById(id: number): Promise<Result<SysPermission>> {
  return request.get(`/system/permission/${id}`);
}

/** 创建权限 */
export function createPermission(data: PermissionFormData): Promise<Result<number>> {
  return request.post('/system/permission', data);
}

/** 更新权限 */
export function updatePermission(id: number, data: PermissionFormData): Promise<Result<void>> {
  return request.put(`/system/permission/${id}`, data);
}

/** 删除权限 */
export function deletePermission(id: number): Promise<Result<void>> {
  return request.delete(`/system/permission/${id}`);
}

/** 获取角色的权限ID列表 */
export function getPermissionIdsByRoleId(roleId: number): Promise<Result<number[]>> {
  return request.get(`/system/permission/role/${roleId}`);
}
