## Context

ERP系统已有销售订单管理（erp-order模块），但缺少发货和退货环节。现有模块：
- erp-order：SalesOrder、OrderItem、CustomerProductPrice
- erp-inventory：StockService（stockIn/stockOut）、InventoryService、WarehouseService
- erp-common：WorkflowService（Flowable 审批）

订单状态流转：DRAFT → CONFIRMED → IN_PRODUCTION → COMPLETED → CLOSED

## Goals / Non-Goals

**Goals:**
- 实现销售发货单，支持部分发货，出库自动扣减库存
- 实现销售退货单，关联发货单，Flowable 审批，入库自动增加库存
- 发货单录入物流公司和运单号

**Non-Goals:**
- 不实现发票/应收管理，留给财务模块
- 不实现物流系统对接（手动录入物流信息）
- 不实现发货通知/拣货单打印

## Decisions

### 1. 发货单放在 erp-order 模块

**决策：** 发货单和退货单的实体、服务、控制器都放在 erp-order 模块。

**理由：**
- 发货单和退货单与销售订单强关联，放在同一模块减少跨模块依赖
- erp-order 模块已有 SalesOrder 基础设施
- 符合现有架构风格（采购入库也在 erp-purchase 模块）

### 2. 发货单状态流转

**决策：** 草稿(0) → 待出库(1) → 已出库(2) → 已签收(3)

**理由：**
- 草稿状态允许修改明细
- 待出库表示拣货完成等待出库
- 已出库确认时自动扣减库存
- 已签收为最终状态

### 3. 部分发货与数量校验

**决策：** 一个订单可创建多个发货单，每次发货校验累计发货数量不超过订单数量。

**理由：**
- 支持大订单分批交付
- 通过查询已发货数量进行校验，类似采购入库的校验逻辑

### 4. 退货审批流程

**决策：** 使用 Flowable 审批，BPMN 流程与不合格品处理类似（单级审批）。

**理由：**
- 保持与现有审批流程一致
- 退货需要销售主管审批确认

### 5. 库存联动方式

**决策：** 通过 ApplicationContext.getBean() 跨模块调用库存服务（与采购入库、工单完工一致）。

**理由：**
- erp-order 不依赖 erp-inventory，保持模块边界清晰
- 库存模块未部署时优雅降级

## Risks / Trade-offs

**风险1：发货数量与订单数量一致性**
→ 通过事务保证，发货单创建时校验数量，出库扣库存失败则回滚

**风险2：退货数量超过发货数量**
→ 退货时校验累计退货数量不超过发货数量

**风险3：库存不足导致出库失败**
→ 出库前检查库存，不足则拒绝出库操作

## 数据库设计

```sql
-- 销售发货单
CREATE TABLE sales_delivery (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  delivery_no VARCHAR(50) NOT NULL UNIQUE COMMENT '发货单号',
  order_id BIGINT NOT NULL COMMENT '销售订单ID',
  customer_id BIGINT COMMENT '客户ID',
  delivery_date DATE COMMENT '发货日期',
  logistics_company VARCHAR(100) COMMENT '物流公司',
  tracking_no VARCHAR(100) COMMENT '运单号',
  warehouse_id BIGINT COMMENT '出库仓库ID',
  status TINYINT DEFAULT 0 COMMENT '0=草稿 1=待出库 2=已出库 3=已签收',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_order_id (order_id),
  INDEX idx_delivery_no (delivery_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售发货单';

-- 销售发货明细
CREATE TABLE sales_delivery_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  delivery_id BIGINT NOT NULL COMMENT '发货单ID',
  product_id BIGINT NOT NULL COMMENT '产品ID',
  quantity INT NOT NULL COMMENT '发货数量',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_delivery_id (delivery_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售发货明细';

-- 销售退货单
CREATE TABLE sales_return (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  return_no VARCHAR(50) NOT NULL UNIQUE COMMENT '退货单号',
  delivery_id BIGINT NOT NULL COMMENT '原发货单ID',
  order_id BIGINT COMMENT '销售订单ID',
  customer_id BIGINT COMMENT '客户ID',
  return_reason VARCHAR(500) COMMENT '退货原因',
  warehouse_id BIGINT COMMENT '退货入库仓库ID',
  status TINYINT DEFAULT 0 COMMENT '0=待审批 1=审批中 2=已通过 3=已驳回 4=已入库',
  process_instance_id VARCHAR(100) COMMENT 'Flowable流程实例ID',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_delivery_id (delivery_id),
  INDEX idx_return_no (return_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售退货单';

-- 销售退货明细
CREATE TABLE sales_return_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  return_id BIGINT NOT NULL COMMENT '退货单ID',
  product_id BIGINT NOT NULL COMMENT '产品ID',
  quantity INT NOT NULL COMMENT '退货数量',
  reason VARCHAR(500) COMMENT '退货原因',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_return_id (return_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售退货明细';
```

## ErrorCode 设计

```java
// 销售发货/退货错误码 (6040-6049)
int DELIVERY_NOT_FOUND = 6040;
int DELIVERY_NO_EXISTS = 6041;
int DELIVERY_QTY_EXCEEDED = 6042;
int DELIVERY_STATUS_ERROR = 6043;
int RETURN_NOT_FOUND = 6044;
int RETURN_NO_EXISTS = 6045;
int RETURN_QTY_EXCEEDED = 6046;
int RETURN_STATUS_ERROR = 6047;
```

## BPMN 流程设计

**sales-return-approval.bpmn20.xml：**
```
提交退货申请 → 销售主管审批 → 结束
                  ├─ 通过 → 退货入库
                  └─ 驳回 → 结束
```
