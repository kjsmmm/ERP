## Why

迭代 0 搭建了后端基础设施（认证、系统管理 CRUD、数据库），但存在三个高优先级缺陷：权限加载硬编码导致 `@PreAuthorize` 失效、权限管理接口缺失、前端仅有最小登录页骨架。当前系统无法真正实现权限控制，也无法支撑任何业务页面开发。迭代 1 的目标是修复这些缺陷，建立完整的前端基础设施，并交付第一个业务模块（客户管理），使系统达到"可登录、可管理客户"的可用状态。

## What Changes

**后端修复：**
- 修复 `UserDetailsServiceImpl` 硬编码问题，从数据库加载用户真实角色和权限
- 新增 `PermissionController`，提供权限树查询和 CRUD 接口
- 修复 `MybatisPlusConfig` 中 `getCurrentUserId()` 返回 null 的问题，使 `createdBy`/`updatedBy` 自动填充生效
- 在现有 Controller 方法上标注 `@Log` 注解，使 AOP 操作日志真正触发

**前端重建：**
- 基于 Vben Admin 2.x 模板重建 `erp-frontend`，替换当前最小骨架
- 实现完整登录流程：Token 存储、刷新机制、路由守卫、权限指令
- 封装 API 请求层：Axios 实例、请求/响应拦截器、统一错误处理
- 实现 Admin 布局：侧边栏导航、顶部面包屑、标签页

**系统管理前端页面（全量）：**
- 用户管理页面（列表、新增、编辑、重置密码、启用/禁用）
- 角色管理页面（列表、新增、编辑、权限树勾选分配）
- 部门管理页面（树形展示、新增、编辑、删除）
- 权限管理页面（权限树展示）
- 操作日志页面（列表、查看详情）
- 个人中心（修改密码、查看基本信息）

**客户管理模块（新增）：**
- 新建 `erp-customer` 后端模块：客户、联系人、跟进记录的完整 CRUD
- 客户列表页面：搜索、筛选、分页、新增/编辑/删除
- 客户详情页面：基本信息 Tab + 联系人 Tab + 跟进记录 Tab

**不在本次范围内：**
- 验证码（Captcha）— 推迟到迭代 1 末期或迭代 2
- 报表与分析

## Capabilities

### New Capabilities
- `frontend-scaffold`: 前端项目基础设施，基于 Vben Admin 2.x 的管理后台布局、路由系统、状态管理、API 封装层、权限指令、Token 管理
- `customer-management`: 客户管理模块，包含客户档案、联系人管理、跟进记录的后端 API 和前端页面

### Modified Capabilities
- `system-management`: 补充权限管理接口（PermissionController）、修复权限加载逻辑（UserDetailsServiceImpl 从数据库加载真实权限）、修复自动填充（createdBy/updatedBy）、标注 @Log 注解；新增系统管理前端页面（用户/角色/部门/权限/日志/个人中心）
- `jwt-auth`: 修复 UserDetailsServiceImpl 中权限硬编码问题，改为从数据库关联查询用户角色和权限码

## Impact

**后端代码：**
- 修改 `erp-auth`：`UserDetailsServiceImpl` 重写权限加载逻辑
- 修改 `erp-system`：新增 `PermissionController`/`PermissionService`/`PermissionServiceImpl`；修改现有 Controller 标注 `@Log`
- 修改 `erp-common`：修复 `MybatisPlusConfig.metaObjectHandler.getCurrentUserId()`
- 新增 `erp-customer` 模块：`pom.xml`、Entity、Mapper、Service、Controller、DTO/VO
- 修改 `erp-backend/pom.xml`：注册 `erp-customer` 模块
- 修改 `erp-boot/pom.xml`：引入 `erp-customer` 依赖

**前端代码：**
- 删除并重建 `erp-frontend/`：基于 Vben Admin 2.x 模板
- 对接后端 API：登录、Token 刷新、系统管理接口、客户管理接口

**数据库：**
- `docker/mysql/init.sql`：新增客户相关表（customer、customer_contact、customer_follow）及初始权限数据

**依赖：**
- 前端新增：Vben Admin 2.x 生态（vue-router、pinia、@vueuse/core 等）
- 后端无新增依赖，仅调整代码逻辑
