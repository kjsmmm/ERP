import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { SysRole, RoleFormData } from '@/types/system';

/** 获取所有角色 */
export function getAllRoles(): Promise<Result<SysRole[]>> {
  return request.get('/system/role/list');
}

/** 获取角色详情 */
export function getRoleById(id: number): Promise<Result<SysRole>> {
  return request.get(`/system/role/${id}`);
}

/** 创建角色 */
export function createRole(data: RoleFormData): Promise<Result<number>> {
  return request.post('/system/role', data);
}

/** 更新角色 */
export function updateRole(id: number, data: RoleFormData): Promise<Result<void>> {
  return request.put(`/system/role/${id}`, data);
}

/** 删除角色 */
export function deleteRole(id: number): Promise<Result<void>> {
  return request.delete(`/system/role/${id}`);
}

/** 分配权限 */
export function assignPermissions(roleId: number, permissionIds: number[]): Promise<Result<void>> {
  return request.put(`/system/role/${roleId}/permissions`, permissionIds);
}
