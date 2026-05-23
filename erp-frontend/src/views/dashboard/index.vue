<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>客户总数</template>
          <div class="stat-value">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>用户总数</template>
          <div class="stat-value">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>角色总数</template>
          <div class="stat-value">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>今日操作</template>
          <div class="stat-value">--</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card class="welcome-card" style="margin-top: 20px;">
      <h2>欢迎使用 ERP 管理系统</h2>
      <p>企业级工厂ERP系统 - 迭代1：用户认证 + 权限 + 客户管理</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  text-align: center;
  padding: 10px 0;
}

.welcome-card h2 {
  margin-bottom: 12px;
}

.welcome-card p {
  color: #909399;
}
</style>
