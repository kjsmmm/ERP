import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { login as loginApi, getUserInfo as getUserInfoApi, logout as logoutApi } from '@/api/auth';
import type { LoginRequest, UserInfo } from '@/types/customer';

export const useUserStore = defineStore('user', () => {
  const accessToken = ref<string>(sessionStorage.getItem('accessToken') || '');
  const refreshToken = ref<string>(sessionStorage.getItem('refreshToken') || '');
  const userInfo = ref<UserInfo | null>(null);

  const isLoggedIn = computed(() => !!accessToken.value);
  const permissions = computed(() => userInfo.value?.permissions || []);
  const roles = computed(() => userInfo.value?.roles || []);

  async function login(data: LoginRequest) {
    const res = await loginApi(data);
    accessToken.value = res.data.accessToken;
    refreshToken.value = res.data.refreshToken;
    sessionStorage.setItem('accessToken', res.data.accessToken);
    sessionStorage.setItem('refreshToken', res.data.refreshToken);
    return res;
  }

  async function fetchUserInfo() {
    const res = await getUserInfoApi();
    userInfo.value = res.data;
    return res.data;
  }

  async function logout() {
    try {
      await logoutApi();
    } catch {
      // 忽略退出接口错误
    }
    clearAuth();
  }

  function clearAuth() {
    accessToken.value = '';
    refreshToken.value = '';
    userInfo.value = null;
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('refreshToken');
  }

  function hasPermission(perm: string): boolean {
    if (roles.value.includes('admin')) return true;
    return permissions.value.includes(perm);
  }

  return {
    accessToken,
    refreshToken,
    userInfo,
    isLoggedIn,
    permissions,
    roles,
    login,
    fetchUserInfo,
    logout,
    clearAuth,
    hasPermission,
  };
});
