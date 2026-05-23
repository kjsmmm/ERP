import axios, { type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';
import router from '@/router';
import type { Result } from '@/types/api';

const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' },
});

// 请求拦截器：自动添加 Token
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const userStore = useUserStore();
    if (userStore.accessToken) {
      config.headers.Authorization = `Bearer ${userStore.accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// 响应拦截器：统一错误处理
service.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    const res = response.data;

    // 业务成功
    if (res.code === 200) {
      return res as any;
    }

    // 401 未授权 → 尝试刷新 Token 或跳转登录
    if (res.code === 401) {
      const userStore = useUserStore();
      userStore.clearAuth();
      router.push('/login');
      ElMessage.error(res.message || '登录已过期，请重新登录');
      return Promise.reject(new Error(res.message));
    }

    // 403 禁止访问
    if (res.code === 403) {
      ElMessage.error(res.message || '权限不足');
      return Promise.reject(new Error(res.message));
    }

    // 其他业务错误
    ElMessage.error(res.message || '请求失败');
    return Promise.reject(new Error(res.message));
  },
  (error) => {
    if (error.response) {
      const { status } = error.response;
      if (status === 401) {
        const userStore = useUserStore();
        userStore.clearAuth();
        router.push('/login');
        ElMessage.error('登录已过期，请重新登录');
      } else if (status === 403) {
        ElMessage.error('权限不足');
      } else if (status === 500) {
        ElMessage.error('服务器错误');
      } else {
        ElMessage.error(error.message || '请求失败');
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接');
    }
    return Promise.reject(error);
  },
);

export default service;
