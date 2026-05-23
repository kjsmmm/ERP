## Why

迭代 0/1/2 全部完成后，对三个迭代的代码进行了全量审计，发现 41 个问题（8 高危、18 中危、15 低危）。高危问题涉及安全漏洞（密码哈希暴露、权限缺失、路径穿越、反序列化风险）和数据完整性（搜索逻辑错误、BOM 环检测遗漏、孤儿数据），必须在进入迭代 3 之前修复。

## What Changes

**安全修复：**
- `SysUser` 敏感字段加 `@JsonIgnore`（password、loginFailCount、lockTime）
- `UserController.changePassword` 加 `@PreAuthorize` 权限校验
- `RedisConfig` 反序列化改用白名单验证器
- JWT Secret 改用环境变量注入，移除硬编码默认值
- `FileUploadUtils` 加路径穿越防护和文件扩展名白名单

**逻辑修复：**
- 客户搜索 `.or()` 改为嵌套条件，修复筛选条件失效
- Service 层 `RuntimeException` 统一改为 `BusinessException`
- 删除产品时级联清理关联图片
- BOM 环检测覆盖新提交批次内部循环
- 客户编码生成改用数据库查询当日最大序号
- 删除客户时检查关联数据

**前端修复：**
- BOM 表单验证回调检查 valid 参数
- 分类编辑排除自身防循环引用
- 状态切换失败回滚 UI
- 分类下拉扁平化显示所有层级

**基础设施修复：**
- `PasswordUtils` 改用 `SecureRandom`
- Refresh Token 改为 `@RequestBody` 传递
- `LogAspect` 设置 `operatorId`
- QueryDTO `pageSize` 加上限
- `AuthController` 加 `@Log` 注解
- Docker 端口统一

## Capabilities

### Modified Capabilities

- `common-framework`: 修复 FileUploadUtils 路径穿越、RedisConfig 反序列化安全、PasswordUtils 随机数安全
- `jwt-auth`: JWT Secret 环境变量化、Refresh Token 传递方式修正
- `system-management`: SysUser 敏感字段隐藏、changePassword 权限修复、LogAspect 完善
- `customer-management`: 搜索逻辑修复、编码生成修复、删除校验补充
- `product-management`: RuntimeException→BusinessException、删除级联清理、分类循环引用防护
- `bom-management`: 环检测增强、quantity 校验

## Impact

- 修改约 20 个后端文件、5 个前端文件
- 不涉及数据库 schema 变更
- 不涉及新增 API 端点
- 不涉及前端路由或权限码变更
