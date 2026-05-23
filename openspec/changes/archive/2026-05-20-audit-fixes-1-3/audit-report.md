# ERP 全量代码审计报告（迭代 0/1/2）

审计日期：2026-05-20

## 总体统计

| 迭代 | 高危 | 中危 | 低危 | 合计 |
|------|------|------|------|------|
| 迭代 0（基础设施） | 2 | 7 | 6 | 15 |
| 迭代 1（认证+客户） | 2 | 3 | 3 | 8 |
| 迭代 2（产品+BOM） | 4 | 8 | 6 | 18 |
| **合计** | **8** | **18** | **15** | **41** |

## 高危问题（必须修复）

| # | 迭代 | 问题 | 位置 |
|---|------|------|------|
| 1 | 0 | `SysUser.password` 未加 `@JsonIgnore`，密码哈希暴露在 API 响应中 | SysUser.java:29 |
| 2 | 0 | `changePassword` 接口缺少 `@PreAuthorize`，任何已登录用户可改任意用户密码 | UserController.java:92 |
| 3 | 0 | Redis 反序列化使用 `LaissezFaireSubTypeValidator`，允许任意类实例化（RCE 风险） | RedisConfig.java:31 |
| 4 | 0 | JWT Secret 硬编码在源码配置文件中 | application.yml:63 |
| 5 | 1 | 客户搜索 `.or()` 逻辑错误，后续筛选条件全部失效 | CustomerServiceImpl.java:50-56 |
| 6 | 2 | Service 层全部使用 `RuntimeException` 而非 `BusinessException`，前端只看到"系统内部错误" | 多个 ServiceImpl |
| 7 | 2 | 删除产品时未清理关联图片记录和磁盘文件 | ProductServiceImpl:136-151 |
| 8 | 2 | BOM 环检测未覆盖新提交批次内部的循环引用 | BomServiceImpl:60-67 |

## 中危问题（建议修复）

| # | 迭代 | 问题 | 位置 |
|---|------|------|------|
| 9 | 0 | `PasswordUtils` 使用 `Math.random()` 生成密码，非密码学安全 | PasswordUtils.java:42 |
| 10 | 0 | Refresh Token 以 query 参数传递，URL 日志泄露 | AuthController.java:44 |
| 11 | 0 | Refresh Token 刷新后旧 Token 未失效，被盗可复用 7 天 | AuthServiceImpl.java:148 |
| 12 | 0 | `LogAspect` 未设置 `operatorId`，日志无法按用户查询 | LogAspect.java:71-79 |
| 13 | 0 | `UserQueryDTO.pageSize` 无上限，可传 999999 导致全表扫描 | UserQueryDTO.java:19 |
| 14 | 0 | `loginFailCount`、`lockTime` 暴露在 API 响应中 | SysUser.java:85,91 |
| 15 | 0 | 无 IP 级登录频率限制，分布式暴力破解风险 | AuthController.java:28 |
| 16 | 1 | 客户编码生成用静态 `AtomicInteger`，重启后编码重复 | CustomerServiceImpl.java:43,134 |
| 17 | 1 | 前端 Token 刷新机制未实现，401 直接清除跳转 | request.ts:36-42 |
| 18 | 1 | 删除客户不检查关联联系人/跟进记录，产生孤儿数据 | CustomerServiceImpl.java:112-118 |
| 19 | 2 | N+1 查询（getProductDetail、getBomByProductId） | ProductServiceImpl:77-91, BomServiceImpl:30-45 |
| 20 | 2 | `FileUploadUtils.delete` 路径穿越漏洞 | FileUploadUtils.java:80-92 |
| 21 | 2 | `FileUploadUtils` 仅校验 Content-Type，可伪造上传 | FileUploadUtils.java:48-51 |
| 22 | 2 | `type-aliases-package` 只扫描 erp-system | application.yml:50 |
| 23 | 2 | `BomItemDTO.quantity` 无正数校验 | BomItemDTO.java:23 |
| 24 | 2 | 前端 Tab 未做懒加载，规格要求 on-demand loading | detail/index.vue:9-108 |
| 25 | 2 | BOM 表单验证回调忽略 `valid` 参数 | detail/index.vue:244-263 |
| 26 | 2 | 分类可设为自身父级（循环引用） | category/index.vue:79-89 |

## 低危问题（可后续优化）

| # | 迭代 | 问题 |
|---|------|------|
| 27 | 0 | Docker 端口不一致（.env.example=3306, docker-compose=3307） |
| 28 | 0 | .env.example JWT Secret 仅 23 字节，HS256 要求 32 字节 |
| 29 | 0 | AuthController login/logout/refresh 缺少 @Log |
| 30 | 0 | PageResult.pages 当 pageSize=0 时除零 |
| 31 | 0 | CORS 允许所有来源 + credentials，生产环境需收紧 |
| 32 | 0 | JWT/MySQL 依赖放在 erp-common 而非合适模块 |
| 33 | 1 | CustomerFollow 用 `transient` 而非 `@TableField(exist=false)` |
| 34 | 1 | 权限删除确认文案与后端行为矛盾 |
| 35 | 1 | 登录页硬编码默认账号密码 |
| 36 | 2 | ProductDetailVO 字段遮蔽父类 |
| 37 | 2 | getCategoryTree 返回扁平列表而非树 |
| 38 | 2 | BeanUtils.copyProperties 可能覆盖 null 字段 |
| 39 | 2 | expandBomTree API 已定义但前端未使用 |
| 40 | 2 | 状态切换失败不回滚 UI |
| 41 | 2 | 分类下拉只显示顶级分类 |
