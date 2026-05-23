## Context

迭代 0 完成了后端基础设施搭建，但审计发现以下问题：
- `UserDetailsServiceImpl` 硬编码返回 `ROLE_USER`，权限系统不工作
- `MybatisPlusConfig.getCurrentUserId()` 返回 null，审计字段不填充
- 前端仅有一个最小登录页，无路由、无状态管理、无布局系统
- 系统管理的所有后端 API 已就绪，但无前端页面

当前后端模块结构：
```
erp-common → erp-system → erp-auth → erp-boot
```
所有系统管理表和初始数据已在 MySQL 中就绪。

## Goals / Non-Goals

**Goals:**
- 权限系统从数据库真实加载，`@PreAuthorize` 生效
- 前端基于 Vben Admin 2.x 建立完整管理后台
- 系统管理 6 个页面全部可用（用户/角色/部门/权限/日志/个人中心）
- 客户管理模块完整交付（后端 + 前端）
- `createdBy`/`updatedBy` 自动填充生效
- `@Log` 注解在所有写操作 Controller 上标注

**Non-Goals:**
- 验证码（Captcha）
- 报表与分析
- 多工厂支持（`factory_id` 保持默认值 1）
- 移动端适配
- 工作流引擎（Flowable）集成

## Decisions

### D1: 权限加载架构

**决策：** `UserDetailsServiceImpl` 通过三表关联查询加载用户权限码。

```
sys_user → sys_user_role → sys_role_permission → sys_permission
```

**实现方式：**
- 在 `SysUserMapper` 中新增自定义方法 `selectUserPermissions(userId)`
- 返回 `List<String>` 权限码列表（如 `system:user:add`, `system:role:edit`）
- `UserDetailsServiceImpl` 查询用户信息 + 权限码，构建 `UserDetails`
- 使用 `SimpleGrantedAuthority` 包装每个权限码

**替代方案考虑：**
- 方案 B: 在 `AuthServiceImpl.login()` 时一次性查出权限存入 Redis → 优点是减少每次请求的查询，缺点是权限变更不能实时生效。当前规模下数据库查询足够快，选择实时查询。

### D2: getCurrentUserId() 实现

**决策：** 从 `SecurityContextHolder` 获取当前认证用户，通过 `UserService.getByUsername()` 查询用户 ID。

**实现方式：**
```java
private Long getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated()) {
        String username = auth.getName();
        SysUser user = userService.getByUsername(username);
        return user != null ? user.getId() : null;
    }
    return null;
}
```

**注意：** `MybatisPlusConfig` 在 `erp-common` 模块，而 `UserService` 在 `erp-system` 模块。为避免循环依赖，改为在 `erp-common` 中定义 `UserIdProvider` 接口，由 `erp-system` 实现并注入。

### D3: 前端集成策略

**决策：** 使用 Vben Admin 2.x 模板重新初始化 `erp-frontend`，替换当前骨架。

**具体步骤：**
1. 备份当前 `erp-frontend/src/views/login/index.vue` 和 `vite.config.ts` 的代理配置
2. 用 Vben Admin 2.x 模板创建新项目
3. 清理示例页面，保留布局、路由、权限、状态管理等基础设施
4. 迁移登录逻辑，对接 `/api/auth/login` 接口
5. 配置 API 代理指向 `localhost:8080`

**替代方案考虑：**
- 方案 B: 手动往现有项目搬运 Vben 组件 → 风险高，容易遗漏隐含依赖，不采用。

### D4: 前端路由与权限映射

**决策：** 路由配置与后端权限码一一对应，通过 Vben Admin 的权限指令控制菜单和按钮可见性。

**权限码命名规范：**
```
模块:操作
──────────────────
system:user:view    用户管理-查看
system:user:add     用户管理-新增
system:user:edit    用户管理-编辑
system:user:delete  用户管理-删除
system:role:view    角色管理-查看
...
customer:view       客户管理-查看
customer:add        客户管理-新增
customer:edit       客户管理-编辑
customer:delete     客户管理-删除
```

**菜单权限：** 控制侧边栏菜单显示
**按钮权限：** 控制页面内操作按钮显示（使用 `v-permission` 指令）

### D5: 客户模块依赖关系

**决策：** 新建 `erp-customer` 模块，依赖 `erp-common` + `erp-system`。

```
erp-boot → erp-customer → erp-system → erp-common
         → erp-auth     → erp-system → erp-common
```

`erp-customer` 依赖 `erp-system` 的原因：跟进记录的 `operator_id` 关联 `sys_user.id`，需要引用 `SysUser` 实体。

### D6: 客户数据模型

**决策：** 三张核心表，采用扁平设计，不引入复杂关联。

```sql
customer           -- 客户主表
├── customer_contact    -- 联系人 (N:1)
└── customer_follow     -- 跟进记录 (N:1, 可选关联联系人)
```

**关键字段设计：**
- `customer_code`: 唯一编码，格式 `CUS-YYYYMMDD-NNN`，系统自动生成
- `customer_type`: TINYINT (1=国内, 2=国外)
- `customer_level`: TINYINT (1=A, 2=B, 3=C, 4=D)
- `is_primary` (联系人): 标记主要联系人，每个客户只有一个
- `follow_type` (跟进记录): TINYINT (1=电话, 2=拜访, 3=邮件, 4=微信)

### D7: 系统管理前端页面模式

**决策：** 统一使用 Vben Admin 的表格 + 弹窗模式。

**页面模式：**
```
┌─────────────────────────────────────────┐
│  搜索区: el-form inline                  │
├─────────────────────────────────────────┤
│  操作区: 新增 / 批量删除 / 导出          │
├─────────────────────────────────────────┤
│  表格区: el-table + 分页                 │
│  行操作: 编辑 / 删除 / 更多              │
└─────────────────────────────────────────┘

弹窗: el-dialog 内嵌表单（新增/编辑复用）
```

**树形页面（部门/权限）：**
```
┌──────────┬────────────────────────────┐
│  树形    │   详情/编辑区              │
│  左侧    │   右侧                     │
│  el-tree │                            │
└──────────┴────────────────────────────┘
```

## Risks / Trade-offs

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| Vben Admin 2.x 集成复杂度超预期 | 前端进度延迟 | 预留 2 天缓冲；如遇阻塞可降级为手动搭建布局 |
| 权限实时查询在高并发下性能问题 | 响应变慢 | 当前 20-100 用户规模无问题；后续可加 Redis 缓存权限 |
| 客户编码自动生成并发冲突 | 编码重复 | 使用数据库序列或 Redis INCR 保证唯一性 |
| 前端重建导致已有登录逻辑丢失 | 功能回退 | 备份关键文件后再重建 |
| Vben Admin 2.x 版本兼容性 | 构建失败 | 锁定 Vben Admin 2.x 稳定版本，不追最新 |

## Open Questions

1. **Vben Admin 2.x 具体版本：** 需要确认使用哪个稳定的 release tag（如 v2.11.x）
2. **客户编码生成策略：** 使用数据库自增序号还是 Redis 原子计数器
3. **文件上传：** 客户是否需要附件功能（合同、资质文件等）— 当前暂不实现，后续按需添加
