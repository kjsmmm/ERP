## Context

本项目是企业级工厂 ERP 系统，从零开始搭建。当前没有任何代码基础，需要建立完整的前后端开发环境和框架。项目面向商业落地，要求技术选型成熟稳定，架构可扩展。

团队规模预估 20-100 用户，单工厂运营（预留多工厂扩展），中文界面，暂无移动端需求。

## Goals / Non-Goals

**Goals:**
- 建立可运行的前后端项目骨架，登录流程跑通
- 采用成熟稳定的技术栈，降低后期维护风险
- 模块化架构，为后续业务迭代打好基础
- Docker 一键启动开发环境
- 完整的系统管理功能（用户、角色、部门、日志）

**Non-Goals:**
- 不实现业务模块（客户、产品、订单等留给后续迭代）
- 不实现复杂的数据权限（迭代0只做基础RBAC）
- 不做生产环境部署配置
- 不做性能优化和高并发处理
- 不集成 Flowable 工作流（迭代0不涉及审批流程）

## Decisions

### Decision 1: 前端框架选择 Vben Admin 2.x

**选择**: Vben Admin 2.x + Vue 3 + Element Plus + TypeScript

**理由**:
- Vben Admin 2.x 成熟稳定，社区资源丰富，坑少
- Element Plus 企业级组件丰富，表格/表单成熟
- Vue 3 Composition API + TypeScript 提供良好的开发体验
- 开箱即用的登录、权限、路由、布局，减少重复造轮子

**备选方案**:
- Vben Admin 5.x：更现代但文档和社区支持不足，商业项目风险高
- 自建脚手架：开发周期长，不适合快速启动

### Decision 2: 后端采用 Maven 多模块架构

**选择**: 多模块结构

```
erp-backend/
├── erp-common/        # 公共模块（工具类、统一响应、异常处理）
├── erp-system/        # 系统管理（用户、角色、部门、日志）
├── erp-auth/          # 认证模块（JWT、登录、注销）
└── erp-boot/          # 启动模块（打包入口）
```

**理由**:
- 模块边界清晰，依赖关系显式声明
- 编译快，只编译改动的模块
- 团队协作友好，不同人负责不同模块
- 未来拆微服务容易

**备选方案**:
- 单模块：简单但边界模糊，代码耦合风险高，不适合商业项目长期维护

### Decision 3: 数据库设计策略

**选择**: 迭代0只建系统管理相关表，业务表按需在后续迭代创建

**核心表**:
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_permission` - 权限表
- `sys_user_role` - 用户-角色关联
- `sys_role_permission` - 角色-权限关联
- `sys_dept` - 部门表
- `sys_log` - 操作日志表
- `sys_config` - 系统配置表

**通用字段**（所有表包含）:
```sql
id            BIGINT       PRIMARY KEY AUTO_INCREMENT
created_by    BIGINT       COMMENT '创建人ID'
created_at    DATETIME     COMMENT '创建时间'
updated_by    BIGINT       COMMENT '更新人ID'
updated_at    DATETIME     COMMENT '更新时间'
deleted       TINYINT      DEFAULT 0 COMMENT '逻辑删除标记'
factory_id    BIGINT       DEFAULT 1 COMMENT '工厂ID（预留多工厂扩展）'
remark        VARCHAR(500) COMMENT '备注'
```

**理由**:
- 需求会变，提前建的表可能要改
- 边做边建，每个迭代的数据库变更加清晰
- factory_id 预留多工厂扩展，当前不感知

### Decision 4: Docker 开发环境

**选择**: MySQL 8.0 + Redis 7 + Adminer

```yaml
services:
  mysql:
    image: mysql:8.0
    ports: ["3306:3306"]
    volumes:
      - mysql-data:/var/lib/mysql
      - ./mysql/init.sql:/docker-entrypoint-initdb.d/init.sql

  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]

  adminer:
    image: adminer
    ports: ["8080:8080"]
```

**理由**:
- MySQL 8.0 稳定可靠，企业级首选
- Redis 7 性能好，用于缓存和 Token 存储
- Adminer 轻量，浏览器访问方便查数据
- Docker Compose 一键启动，环境一致性

### Decision 5: JWT 认证方案

**选择**: Access Token + Refresh Token，Redis 存储活跃 Token

**Token 设计**:
- Access Token：有效期 15 分钟，用于接口认证
- Refresh Token：有效期 7 天，用于刷新 Access Token
- Redis 存储活跃 Token，支持主动注销

**安全策略**:
- 密码 BCrypt 哈希 + 随机盐值
- 登录失败 5 次锁定 30 分钟
- Spring Security 6 集成

**理由**:
- 双 Token 机制兼顾安全性和用户体验
- Redis 存储支持 Token 主动失效
- 迭代0一次到位，避免后续返工

### Decision 6: 公共框架设计

**统一响应格式**:
```java
public class Result<T> {
    private int code;       // 状态码
    private String message; // 提示信息
    private T data;         // 数据
}
```

**全局异常处理**:
- BusinessException：业务异常，自定义错误码
- MethodArgumentNotValidException：参数校验异常
- AccessDeniedException：权限不足异常
- Exception：未知异常兜底

**日志框架**: SLF4J + Logback，按级别输出到文件

**接口文档**: SpringDoc (OpenAPI 3)，自动生成 Swagger UI

**跨域配置**: 开发环境允许 localhost，生产环境配置域名白名单

## Risks / Trade-offs

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| Vben Admin 2.x 学习曲线 | 前端开发效率 | 第一个模块重点打磨，形成模板 |
| 多模块 Maven 配置复杂 | 初期搭建时间 | 先搭骨架，按需添加模块 |
| JWT Token 刷新逻辑 | 用户体验 | 前端 Axios 拦截器自动刷新 |
| 数据库表结构设计 | 后期重构成本 | 通用字段预留，按需迭代 |

## Open Questions

- Vben Admin 2.x 的具体版本号选择（最新稳定版）
- 前端主题和布局定制程度
- 日志文件保留策略和大小限制
- 接口文档的访问权限控制
