## 1. Docker 开发环境搭建

- [x] 1.1 创建 `docker/` 目录结构
- [x] 1.2 编写 `docker-compose.yml`（MySQL 8.0 + Redis 7 + Adminer）
- [x] 1.3 创建 `docker/mysql/init.sql` 初始化脚本（系统管理表）
- [x] 1.4 创建 `.env.example` 环境变量模板
- [x] 1.5 创建 `.gitignore` 排除 `.env` 和数据卷
- [x] 1.6 验证 Docker 环境一键启动正常

## 2. 后端项目初始化

- [x] 2.1 创建 `erp-backend/` 目录和父 `pom.xml`（版本管理）
- [x] 2.2 创建 `erp-common` 模块（pom.xml + 基础包结构）
- [x] 2.3 创建 `erp-system` 模块（pom.xml + 基础包结构）
- [x] 2.4 创建 `erp-auth` 模块（pom.xml + 基础包结构）
- [x] 2.5 创建 `erp-boot` 模块（pom.xml + 启动类）
- [x] 2.6 配置 `application.yml`（数据库、Redis、JWT、日志）
- [x] 2.7 配置 `application-dev.yml` 开发环境专用配置
- [x] 2.8 验证后端项目编译通过

## 3. 公共框架实现（erp-common）

- [x] 3.1 实现 `Result<T>` 统一响应类
- [x] 3.2 实现 `PageResult<T>` 分页响应类
- [x] 3.3 实现 `BusinessException` 业务异常类
- [x] 3.4 实现 `GlobalExceptionHandler` 全局异常处理器
- [x] 3.5 实现 `BaseEntity` 基础实体类（通用字段）
- [x] 3.6 配置 Logback 日志框架（控制台 + 文件输出）
- [x] 3.7 配置 CORS 跨域（开发环境 localhost）
- [x] 3.8 配置 SpringDoc (OpenAPI 3) 接口文档
- [x] 3.9 配置 MyBatis-Plus（分页插件、逻辑删除）
- [x] 3.10 配置 Redis 连接和序列化

## 4. JWT 认证实现（erp-auth）

- [x] 4.1 实现 `JwtUtils` 工具类（生成/解析/验证 Token）
- [x] 4.2 实现 `RedisUtils` 工具类（Token 存储/删除/过期）
- [x] 4.3 实现 `JwtAuthenticationFilter` 认证过滤器
- [x] 4.4 实现 `SecurityConfig` Spring Security 配置
- [x] 4.5 实现 `AuthController`（登录、注销、刷新 Token）
- [x] 4.6 实现 `AuthService` 接口和 `AuthServiceImpl`
- [x] 4.7 实现登录失败次数限制（5次锁定30分钟）
- [x] 4.8 实现密码 BCrypt 加密工具类
- [x] 4.9 配置公开端点白名单（登录、Swagger等）

## 5. 系统管理模块实现（erp-system）

- [x] 5.1 实现 `SysUser` 实体类和 `UserMapper`
- [x] 5.2 实现 `SysRole` 实体类和 `RoleMapper`
- [x] 5.3 实现 `SysPermission` 实体类和 `PermissionMapper`
- [x] 5.4 实现 `SysDept` 实体类和 `DeptMapper`
- [x] 5.5 实现 `SysLog` 实体类和 `LogMapper`
- [x] 5.6 实现 `UserService` 接口和 `UserServiceImpl`
- [x] 5.7 实现 `UserController`（用户 CRUD）
- [x] 5.8 实现 `RoleService` 接口和 `RoleServiceImpl`
- [x] 5.9 实现 `RoleController`（角色 CRUD + 权限分配）
- [x] 5.10 实现 `DeptService` 接口和 `DeptServiceImpl`
- [x] 5.11 实现 `DeptController`（部门 CRUD + 树形查询）
- [x] 5.12 实现 `LogService` 接口和 `LogServiceImpl`
- [x] 5.13 实现 `LogController`（日志查询）
- [x] 5.14 实现 `@Log` 操作日志注解和 AOP 切面

## 6. 数据库初始化

- [x] 6.1 编写 `sys_user` 表建表语句
- [x] 6.2 编写 `sys_role` 表建表语句
- [x] 6.3 编写 `sys_permission` 表建表语句
- [x] 6.4 编写 `sys_user_role` 关联表建表语句
- [x] 6.5 编写 `sys_role_permission` 关联表建表语句
- [x] 6.6 编写 `sys_dept` 表建表语句
- [x] 6.7 编写 `sys_log` 表建表语句
- [x] 6.8 编写 `sys_config` 表建表语句
- [x] 6.9 插入初始数据（管理员用户、默认角色、默认部门）
- [x] 6.10 验证数据库初始化脚本执行正常

## 7. 前端项目初始化

- [x] 7.1 使用 Vben Admin 2.x 模板创建 `erp-frontend/` 项目
- [x] 7.2 安装依赖（`npm install`）
- [x] 7.3 配置开发环境代理（指向后端 8080 端口）
- [x] 7.4 配置 Axios 请求拦截器（自动添加 Token）
- [x] 7.5 配置 Axios 响应拦截器（Token 过期自动刷新）
- [x] 7.6 验证前端开发服务器启动正常

## 8. 前后端联调验证

- [x] 8.1 启动 Docker 环境（MySQL + Redis）
- [x] 8.2 启动后端应用（erp-boot）
- [x] 8.3 启动前端开发服务器
- [x] 8.4 验证登录页面正常显示
- [x] 8.5 验证管理员账号登录成功
- [x] 8.6 验证 JWT Token 生成和存储正常
- [x] 8.7 验证 Token 刷新机制正常
- [x] 8.8 验证注销功能正常
- [x] 8.9 验证用户管理页面 CRUD 功能
- [x] 8.10 验证角色管理页面功能
- [x] 8.11 验证部门管理页面功能
- [x] 8.12 验证操作日志查询功能
- [x] 8.13 验证 Swagger UI 接口文档可访问

## 9. 文档和收尾

- [x] 9.1 编写 `README.md` 项目说明文档
- [x] 9.2 编写开发环境搭建指南
- [x] 9.3 编写 API 接口说明文档
- [x] 9.4 代码审查和重构优化
- [x] 9.5 提交代码到版本控制系统
