## Why

本项目是一个企业级工厂 ERP 系统，需要从零搭建前后端基础设施。迭代 0 的目标是建立可运行的项目骨架，为后续业务模块开发提供稳定的基础框架。没有这个基础，后续迭代无法进行。

## What Changes

- 前端项目初始化：基于 Vben Admin 2.x + Vue 3 + Element Plus + TypeScript 搭建管理后台
- 后端项目初始化：Spring Boot 3.2 多模块架构（erp-common / erp-system / erp-auth / erp-boot）
- 数据库初始化：MySQL 8.0 建表脚本（系统管理相关表：用户、角色、权限、部门、日志）
- Docker 开发环境：docker-compose.yml 编排 MySQL + Redis + Adminer
- 统一响应格式：Result<T> 封装，全局异常处理
- JWT 认证方案：Access Token + Refresh Token，Redis 存储活跃 Token
- 系统管理模块：用户管理、角色管理、部门管理、操作日志
- 接口文档：SpringDoc (OpenAPI 3) 自动生成
- 跨域配置、日志框架配置（SLF4J + Logback）
- 前后端联调验证：登录流程跑通

## Capabilities

### New Capabilities

- `project-scaffold`: 前后端项目骨架搭建（Vben Admin 2.x + Spring Boot 3 多模块）
- `docker-dev-env`: Docker 开发环境编排（MySQL + Redis + Adminer）
- `common-framework`: 公共框架（统一响应、全局异常、日志、跨域、接口文档）
- `jwt-auth`: JWT 认证方案（登录、注销、Token 刷新、密码加密）
- `system-management`: 系统管理模块（用户、角色、部门、操作日志 CRUD）

### Modified Capabilities

（无，这是从零开始的新项目）

## Impact

- 创建前端项目目录：`erp-frontend/`
- 创建后端项目目录：`erp-backend/`（含多模块：common、system、auth、boot）
- 创建 Docker 配置目录：`docker/`
- 创建数据库脚本目录：`docker/mysql/`
- 依赖引入：Spring Boot 3.2、MyBatis-Plus、Redis、JWT (jjwt)、Spring Security 6
- 前端依赖：Vben Admin 2.x、Element Plus、Axios、Pinia、Vue Router
