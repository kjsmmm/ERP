# 迭代 3b：Flowable 集成 + 客户定价 + 订单变更审批

## Why

迭代 3a 完成了订单和库存的核心主干。迭代 3b 在此基础上引入 Flowable 工作流引擎，实现订单变更审批流程，并增加客户级别定价能力。这为后续迭代的采购审批、质检流程建立基础模式。

## What Changes

### 1. Flowable 工作流集成
- 引入 flowable-spring-boot-starter，自动建表
- 配置 Flowable Spring Boot 集成
- WorkflowService 封装（发起、查询、审批、驳回）
- WorkflowController（待办/已办/审批接口）

### 2. 订单变更审批流程
- 第一个 BPMN 流程定义：申请人提交 → 直接主管审批 → 完成
- 已确认订单编辑时触发审批流程
- 审批通过后订单变更生效，驳回则回退

### 3. 客户级别定价
- 产品基础价格表（已有 standardPrice）
- 客户-产品价格表（不同客户不同价格）
- 订单创建时自动带出客户专属价格（客户价格 > 标准售价）

### 4. 库存预警
- 安全库存配置（按产品+仓库）
- 低于安全库存的产品预警查询

## Capabilities

- **workflow-engine**: Flowable 集成基础能力 + 审批服务封装
- **order-change-approval**: 订单变更审批流程（第一个业务流程示例）
- **customer-pricing**: 客户级别产品定价

## Impact

### 修改模块
- `erp-order`：增加变更审批联动、客户价格带出逻辑
- `erp-inventory`：增加安全库存预警
- `erp-boot`：引入 Flowable 依赖
- `erp-frontend`：新增审批待办页面、客户价格管理页

### 新增依赖
- Flowable Spring Boot Starter 7.x

### 数据库
- 新增表：customer_product_price
- Flowable 自动创建：ACT_* 系列表
