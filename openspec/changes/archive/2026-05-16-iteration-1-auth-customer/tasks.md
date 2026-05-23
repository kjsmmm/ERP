## 1. 后端修复 - 权限体系

- [x] 1.1 修复 `UserDetailsServiceImpl`：从数据库关联查询用户角色和权限码（sys_user → sys_user_role → sys_role_permission → sys_permission），替代硬编码 `ROLE_USER`
- [x] 1.2 新增 `PermissionService` 接口和 `PermissionServiceImpl`：实现权限树查询、CRUD 操作
- [x] 1.3 新增 `PermissionController`：GET /api/system/permission/tree、POST、PUT、DELETE 接口
- [x] 1.4 修复 `MybatisPlusConfig.getCurrentUserId()`：引入 `UserIdProvider` 接口避免循环依赖，由 `erp-system` 模块实现从 SecurityContextHolder 获取用户 ID
- [x] 1.5 在所有现有 Controller 的写操作方法上标注 `@Log` 注解（UserController、RoleController、DeptController、LogController 的 create/update/delete 方法）

## 2. 后端修复 - 数据库更新

- [x] 2.1 更新 `docker/mysql/init.sql`：新增客户相关表（customer、customer_contact、customer_follow）
- [x] 2.2 更新 `docker/mysql/init.sql`：新增客户管理相关权限数据（customer:view、customer:add、customer:edit、customer:delete）
- [x] 2.3 更新 `docker/mysql/init.sql`：新增权限管理相关权限数据（system:permission:view、system:permission:add、system:permission:edit、system:permission:delete）
- [x] 2.4 重建 Docker MySQL 容器使新表结构生效

## 3. 后端 - 客户管理模块

- [x] 3.1 创建 `erp-customer` 模块：新建 pom.xml，依赖 erp-common + erp-system + lombok
- [x] 3.2 修改 `erp-backend/pom.xml`：modules 中注册 erp-customer
- [x] 3.3 修改 `erp-boot/pom.xml`：dependencies 中引入 erp-customer
- [x] 3.4 创建 Entity：Customer、CustomerContact、CustomerFollow（继承 BaseEntity）
- [x] 3.5 创建 Mapper：CustomerMapper、ContactMapper、FollowMapper（继承 BaseMapper）
- [x] 3.6 创建 DTO：CustomerDTO、CustomerQueryDTO、ContactDTO、FollowDTO
- [x] 3.7 创建 VO：CustomerDetailVO（聚合客户信息+联系人列表）
- [x] 3.8 创建枚举：CustomerType、CustomerLevel、FollowType
- [x] 3.9 创建 Service 接口：CustomerService、ContactService、FollowService
- [x] 3.10 实现 CustomerServiceImpl：CRUD + 分页查询 + 客户编码自动生成（CUS-YYYYMMDD-NNN）
- [x] 3.11 实现 ContactServiceImpl：CRUD + 主要联系人互斥逻辑
- [x] 3.12 实现 FollowServiceImpl：CRUD + 分页查询 + 自动设置 operator_id
- [x] 3.13 创建 CustomerController：客户 CRUD + 启用/停用，标注 @Log
- [x] 3.14 创建 ContactController：联系人 CRUD，标注 @Log
- [x] 3.15 创建 FollowController：跟进记录 CRUD，标注 @Log
- [x] 3.16 编译验证：确保 erp-customer 模块编译通过

## 4. 前端 - Vben Admin 集成

- [x] 4.1 备份当前 erp-frontend 关键文件（login/index.vue、vite.config.ts 代理配置）
- [x] 4.2 用 Vue 3 + Element Plus + TypeScript 重建 erp-frontend（替代 Vben Admin 2.x 模板，更轻量可控）
- [x] 4.3 构建布局、路由、权限、状态管理基础设施
- [x] 4.4 配置 API 代理指向 localhost:8080
- [x] 4.5 配置 TypeScript 类型定义（API 响应类型、实体类型）
- [x] 4.6 验证前端基础框架可正常构建

## 5. 前端 - 登录与认证

- [x] 5.1 对接后端登录接口 /api/auth/login，适配请求/响应格式
- [x] 5.2 实现 Token 存储：access token 存 sessionStorage
- [x] 5.3 实现 Axios 请求拦截器：自动添加 Authorization header
- [x] 5.4 实现 Axios 响应拦截器：401 跳转登录，403 提示权限不足
- [x] 5.5 实现 Token 刷新机制（后端支持 /auth/refresh）
- [x] 5.6 实现登录后获取用户信息和权限列表（/auth/info 接口）
- [x] 5.7 实现退出登录：清除 Token + 跳转登录页
- [x] 5.8 验证登录→跳转→退出完整流程

## 6. 前端 - 路由与权限

- [x] 6.1 配置系统管理路由：/system/user、/system/role、/system/dept、/system/permission、/system/log
- [x] 6.2 配置客户管理路由：/customer/list、/customer/detail/:id
- [x] 6.3 配置个人中心路由：/profile
- [x] 6.4 实现路由守卫：未登录重定向到登录页
- [x] 6.5 实现动态菜单生成：根据用户权限码过滤菜单项
- [x] 6.6 实现 v-permission 指令：根据权限码控制按钮显示

## 7. 前端 - API 封装层

- [x] 7.1 封装 API 模块：api/system/user.ts、api/system/role.ts、api/system/dept.ts、api/system/permission.ts、api/system/log.ts
- [x] 7.2 封装 API 模块：api/customer/customer.ts、api/customer/contact.ts、api/customer/follow.ts
- [x] 7.3 封装通用 API 工具函数：request.ts（Axios 实例 + 拦截器）

## 8. 前端 - 用户管理页面

- [x] 8.1 用户列表页：表格展示（用户名、昵称、部门、状态、创建时间）+ 分页
- [x] 8.2 用户列表页：搜索区（用户名、真实姓名、状态筛选）
- [x] 8.3 用户新增/编辑弹窗：表单（用户名、密码、昵称、邮箱、手机、性别、部门树选择、角色多选）
- [x] 8.4 用户操作：重置密码（确认弹窗 + 显示新密码）、启用/禁用开关、删除确认

## 9. 前端 - 角色管理页面

- [x] 9.1 角色列表页：表格展示（角色名、角色编码、状态、排序）+ 分页
- [x] 9.2 角色新增/编辑弹窗：表单（角色名、角色编码、排序、状态、备注）
- [x] 9.3 角色权限分配：权限树弹窗 + 复选框勾选 + 保存

## 10. 前端 - 部门管理页面

- [x] 10.1 部门树形展示：左侧 el-tree 组件
- [x] 10.2 部门详情/编辑：右侧信息展示 + 新增/编辑/删除操作
- [x] 10.3 部门表单：部门名称、编码、父部门、排序、负责人、电话、邮箱

## 11. 前端 - 权限管理页面

- [x] 11.1 权限树展示：树形结构（目录/菜单/按钮三级）
- [x] 11.2 权限新增/编辑弹窗：表单（权限名称、权限编码、类型、路径、图标、排序）
- [x] 11.3 权限删除：检查子权限后确认删除

## 12. 前端 - 操作日志页面

- [x] 12.1 日志列表页：表格展示（模块、操作、操作人、IP、状态、时间）+ 分页
- [x] 12.2 日志搜索区：模块、操作人、状态筛选
- [x] 12.3 日志详情弹窗：展示完整请求参数、响应结果、错误信息

## 13. 前端 - 个人中心

- [x] 13.1 个人信息展示：用户名、昵称、真实姓名、邮箱、手机、角色（只读）
- [x] 13.2 修改密码表单：旧密码、新密码、确认密码 + 校验

## 14. 前端 - 客户管理页面

- [x] 14.1 客户列表页：表格展示（客户编码、名称、类型、行业、等级、状态）+ 分页
- [x] 14.2 客户列表页：搜索区（名称/编码搜索、类型/等级/状态筛选）+ 重置
- [x] 14.3 客户新增/编辑弹窗：表单（名称、类型、行业、等级、来源、税号、开户行、账号、账期、信用额度、地址、备注）
- [x] 14.4 客户操作：启用/禁用、删除确认、查看详情跳转
- [x] 14.5 客户详情页：基本信息 Tab（展示客户资料）
- [x] 14.6 客户详情页：联系人 Tab（列表 + 新增/编辑/删除弹窗，主要联系人标记）
- [x] 14.7 客户详情页：跟进记录 Tab（时间线展示 + 新增弹窗，类型标签）

## 15. 联调与验证

- [x] 15.1 后端编译验证：全模块 mvn clean package 通过
- [x] 15.2 前端构建验证：npm run build 通过
- [x] 15.3 登录流程验证：登录→获取权限→动态菜单→退出
- [x] 15.4 系统管理页面验证：用户/角色/部门/权限/日志 CRUD 正常
- [x] 15.5 客户管理页面验证：客户 CRUD + 联系人 + 跟进记录正常
- [x] 15.6 权限控制验证：不同角色用户看到不同菜单和按钮
