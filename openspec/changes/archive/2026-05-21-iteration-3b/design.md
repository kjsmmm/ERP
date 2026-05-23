# 迭代 3b 技术设计

## D1: Flowable 集成方式

采用 Spring Boot Starter 方式集成 Flowable 7.x：

- 依赖：`flowable-spring-boot-starter`
- 数据库：Flowable 自动创建 ACT_* 表（与业务库共用）
- 流程定义：BPMN 2.0 XML 文件放在 `resources/processes/` 目录
- 服务封装：`WorkflowService` 封装发起、查询、审批、驳回操作

Flowable 相关代码放在 `erp-common` 模块（通用能力）：
```
erp-common/
├── workflow/
│   ├── WorkflowService.java
│   ├── WorkflowConfig.java
│   └── WorkflowController.java
```

## D2: 订单变更审批流程

第一个 BPMN 流程定义：

```
┌──────────┐     ┌──────────┐     ┌──────────┐
│  申请人   │────▶│ 直接主管  │────▶│   结束    │
│  提交    │     │  审批    │     │          │
└──────────┘     └──────────┘     └──────────┘
```

触发时机：已确认订单被编辑时
- 订单状态变为 PENDING_CHANGE
- 发起审批流程
- 审批通过：变更生效，状态回到 CONFIRMED
- 审批驳回：变更丢弃，状态回到 CONFIRMED

## D3: 客户级别定价

新增 `customer_product_price` 表。订单创建时价格取值优先级：

```
客户专属价格 > 产品标准售价
```

查询逻辑：先查 customer_product_price，无记录则取 product.standard_price。

## D4: 库存预警

在 Inventory 实体中增加 `safety_stock` 字段。预警查询：

```sql
SELECT * FROM inventory WHERE on_hand_qty < safety_stock AND safety_stock > 0
```

## D5: 修改范围

迭代 3b 不新增后端模块，而是在已有模块上扩展：
- `erp-common`：新增 workflow 包
- `erp-order`：OrderService 增加变更审批联动、客户价格带出
- `erp-inventory`：增加安全库存字段和预警查询
- `erp-boot`：引入 Flowable 依赖
- `erp-frontend`：新增审批待办页、客户价格管理页
