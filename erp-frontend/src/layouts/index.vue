<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '210px'" class="layout-aside">
      <div class="logo" @click="router.push('/')">
        <span v-if="!isCollapse">ERP 管理系统</span>
        <span v-else>ERP</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="route.path"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <template v-for="item in menuRoutes" :key="item.path">
            <!-- 单级菜单 -->
            <el-menu-item
              v-if="!item.children || item.children.length === 1"
              :index="getMenuPath(item)"
            >
              <el-icon v-if="getMenuMeta(item)?.icon">
                <component :is="getMenuMeta(item)?.icon" />
              </el-icon>
              <template #title>{{ getMenuMeta(item)?.title }}</template>
            </el-menu-item>

            <!-- 多级菜单 -->
            <el-sub-menu v-else :index="item.path">
              <template #title>
                <el-icon v-if="item.meta?.icon">
                  <component :is="item.meta.icon" />
                </el-icon>
                <span>{{ item.meta?.title }}</span>
              </template>
              <template v-for="child in item.children" :key="child.path">
                <el-menu-item
                  v-if="!child.meta?.hidden && hasRoutePermission(child)"
                  :index="`${item.path}/${child.path}`"
                >
                  <el-icon v-if="child.meta?.icon">
                    <component :is="child.meta.icon" />
                  </el-icon>
                  <template #title>{{ child.meta?.title }}</template>
                </el-menu-item>
              </template>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <!-- 面包屑 -->
          <el-breadcrumb separator="/">
            <el-breadcrumb-item
              v-for="item in breadcrumbs"
              :key="item.path"
              :to="item.path"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="30" icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { constantRoutes } from '@/router';
import { Fold, Expand } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const isCollapse = ref(false);

// 过滤出有权限的菜单路由
const menuRoutes = computed(() => {
  return constantRoutes.filter((r) => {
    if (r.meta?.hidden) return false;
    if (!r.children) return false;
    // 根路由（如 Dashboard）
    if (r.path === '/' || r.path === '/profile') return true;
    // 有子菜单的路由
    return r.children.some((child) => !child.meta?.hidden && hasRoutePermission(child));
  });
});

function hasRoutePermission(route: any): boolean {
  if (!route.meta?.permission) return true;
  return userStore.hasPermission(route.meta.permission);
}

function getMenuPath(item: any): string {
  if (item.children?.length === 1) {
    return `${item.path}/${item.children[0].path}`.replace('//', '/');
  }
  return item.redirect || item.path;
}

function getMenuMeta(item: any) {
  if (item.children?.length === 1) {
    return item.children[0].meta;
  }
  return item.meta;
}

// 面包屑
const breadcrumbs = computed(() => {
  const matched = route.matched.filter((item) => item.meta?.title);
  return matched.map((item) => ({
    path: item.path,
    title: item.meta.title as string,
  }));
});

function handleCommand(command: string) {
  if (command === 'profile') {
    router.push('/profile');
  } else if (command === 'logout') {
    userStore.logout();
    router.push('/login');
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  white-space: nowrap;
  overflow: hidden;
  background-color: #2b2f3a;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #dcdfe6;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}

.collapse-btn:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.username {
  font-size: 14px;
}

.layout-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
