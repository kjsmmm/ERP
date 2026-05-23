/** 系统用户 */
export interface SysUser {
  id: number;
  username: string;
  nickname: string;
  realName: string;
  email: string;
  phone: string;
  avatar: string;
  gender: number;
  deptId: number;
  deptName: string;
  status: number;
  loginIp: string;
  loginDate: string;
  roles: SysRole[];
  createdAt: string;
  remark: string;
}

/** 用户表单 */
export interface UserFormData {
  id?: number;
  username: string;
  password?: string;
  nickname: string;
  realName: string;
  email: string;
  phone: string;
  gender: number;
  deptId: number | null;
  roleIds: number[];
  remark: string;
}

/** 用户查询参数 */
export interface UserQuery {
  username?: string;
  realName?: string;
  status?: number;
  deptId?: number;
  pageNum?: number;
  pageSize?: number;
}

/** 角色 */
export interface SysRole {
  id: number;
  roleName: string;
  roleCode: string;
  sortOrder: number;
  dataScope: number;
  status: number;
  remark: string;
  createdAt: string;
}

/** 角色表单 */
export interface RoleFormData {
  id?: number;
  roleName: string;
  roleCode: string;
  sortOrder: number;
  status: number;
  remark: string;
}

/** 部门 */
export interface SysDept {
  id: number;
  parentId: number;
  deptName: string;
  deptCode: string;
  sortOrder: number;
  remark: string;
  leader: string;
  phone: string;
  email: string;
  status: number;
  children: SysDept[];
  createdAt: string;
}

/** 部门表单 */
export interface DeptFormData {
  id?: number;
  parentId: number;
  deptName: string;
  deptCode: string;
  sortOrder: number;
  leader: string;
  phone: string;
  email: string;
  remark: string;
}

/** 权限 */
export interface SysPermission {
  id: number;
  parentId: number;
  permName: string;
  permCode: string;
  permType: number;
  path: string;
  component: string;
  icon: string;
  sortOrder: number;
  visible: number;
  status: number;
  children: SysPermission[];
}

/** 权限表单 */
export interface PermissionFormData {
  id?: number;
  parentId: number;
  permName: string;
  permCode: string;
  permType: number;
  path: string;
  component: string;
  icon: string;
  sortOrder: number;
  visible: number;
  remark: string;
}

/** 操作日志 */
export interface SysLog {
  id: number;
  module: string;
  operation: string;
  method: string;
  requestUrl: string;
  requestMethod: string;
  requestParams: string;
  responseResult: string;
  operatorId: number;
  operatorName: string;
  operatorIp: string;
  executeTime: number;
  status: number;
  errorMsg: string;
  createdAt: string;
}

/** 日志查询参数 */
export interface LogQuery {
  module?: string;
  operation?: string;
  operatorName?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}
