## Context

三个迭代全量审计发现 41 个问题。本次变更集中修复所有高危和中危问题，低危问题中修复成本低的也一并处理。不改变已有功能行为，只修复缺陷和安全漏洞。

## Goals / Non-Goals

**Goals:**
- 修复全部 8 个高危安全和数据完整性问题
- 修复全部 18 个中危问题中的 12 个（剩余 6 个需要较大重构，留后续迭代）
- 修复 7 个低危问题中修复成本低的
- 保持向后兼容，不破坏现有 API 契约

**Non-Goals:**
- 不重构模块依赖结构（POM 依赖位置调整）
- 不实现 IP 级频率限制（需要引入新依赖）
- 不实现 Token 刷新机制（前端较大重构）
- 不实现 N+1 查询优化（需要重写 SQL，风险较高）
- 不处理前端 Tab 懒加载（需要重构数据加载流程）

## Decisions

### D1: BusinessException 统一方案

**选择:** 在各 ServiceImpl 中将 `new RuntimeException(msg)` 替换为 `new BusinessException(ErrorCode.XXX, msg)`。

**实现方式:**
- 已有 `ErrorCode` 枚举定义了业务错误码
- 已有 `GlobalExceptionHandler` 处理 `BusinessException`
- 需要在 `ErrorCode` 中补充缺失的错误码（如 PRODUCT_NOT_FOUND、BOM_CIRCULAR_REFERENCE 等）

### D2: 客户编码生成改进

**选择:** 查询数据库当日最大序号，在此基础上 +1。

**实现方式:**
```sql
SELECT customer_code FROM customer
WHERE customer_code LIKE 'CUS-20260520-%'
ORDER BY customer_code DESC LIMIT 1
```
从结果中解析序号部分，+1 后生成新编码。无需 Redis 依赖，重启安全。

### D3: BOM 环检测增强

**选择:** 在检查 DB 已有关系的基础上，额外检查新提交批次内部的循环。

**实现方式:**
- 构建新批次的有向图（materialId → productId）
- 对每个 materialId 递归遍历，若回到已访问节点则报错
- 与现有 DB 检查合并，任一发现环即拒绝保存

### D4: 文件上传安全加固

**选择:** 在 `FileUploadUtils` 中同时校验 Content-Type 和文件扩展名。

**允许扩展名白名单:** jpg, jpeg, png, gif, webp
**路径穿越防护:** 在 `delete()` 方法中用 `Paths.get().normalize()` 校验结果是否在 basePath 内。

### D5: Redis 反序列化安全

**选择:** 将 `LaissezFaireSubTypeValidator` 替换为 `BasicPolymorphicTypeValidator`，只允许 `com.erp.**` 包下的类反序列化。

### D6: JWT Secret 环境变量化

**选择:** `application.yml` 中 JWT Secret 改为 `${JWT_SECRET:}`（无默认值），启动时若未设置则抛异常。`.env.example` 中提供生成密钥的指引。

## Risks / Trade-offs

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| ErrorCode 新增错误码可能遗漏 | 部分场景返回通用错误码 | 逐一检查每个 RuntimeException 的语义 |
| Redis 白名单过严导致反序列化失败 | 缓存读取异常 | 白名单覆盖所有可能被缓存的实体类 |
| 客户编码在高并发下仍可能重复 | DuplicateKeyException | DB 唯一索引兜底，捕获异常重试一次 |
