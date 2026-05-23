import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { LoginRequest, LoginResult, UserInfo } from '@/types/customer';

/** 登录 */
export function login(data: LoginRequest): Promise<Result<LoginResult>> {
  return request.post('/auth/login', data);
}

/** 刷新 Token */
export function refreshToken(refreshToken: string): Promise<Result<LoginResult>> {
  return request.post('/auth/refresh', null, { params: { refreshToken } });
}

/** 获取当前用户信息 */
export function getUserInfo(): Promise<Result<UserInfo>> {
  return request.get('/auth/info');
}

/** 退出登录 */
export function logout(): Promise<Result<void>> {
  return request.post('/auth/logout');
}
