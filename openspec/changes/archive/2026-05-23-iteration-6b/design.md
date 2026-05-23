## Context

ERP系统已完成销售发货（iteration-6a）和采购入库（iteration-5a），资金流动的管理是缺失环节。现有模块：
- erp-order：SalesDelivery（发货出库确认）
- erp-purchase：PurchaseReceipt（入库确认）
- erp-inventory：StockService（库存出入库）
- erp-common：无财务相关组件

需要新建 erp-finance 模块，通过跨模块调用与发货/入库联动。

## Goals / Non-Goals

**Goals:**
- 新建 erp-finance 模块，管理应收应付
- 销售发货出库确认时自动生成应收单（通过 ApplicationContext 回调）
- 采购入库时自动生成应付单
- 应收/应付单上记录发票信息
- 支持多次部分收款/付款核销
- 应收/应付状态：未收付 → 部分收付 → 已收付

**Non-Goals:**
- 不实现成本核算（留给后续迭代）
- 不实现预收/预付
- 不实现账龄分析（先做基础记录和核销）

## Decisions

### 1. 新建 erp-finance 模块

**决策：** 新建独立的 erp-finance 模块，不放在 erp-order 或 erp-purchase 中。

**理由：**
- 财务管理是独立业务域，后续可扩展成本核算、总账等
- 应收横跨销售发货，应付横跨采购入库，适合独立模块
- 符合现有架构风格（每个业务域独立模块）

### 2. 业务驱动自动生成应收/应付

**决策：** 销售发货出库确认时自动创建应收单，采购入库确认时自动创建应付单。

**实现：**
- SalesDeliveryServiceImpl.shipOut() 中通过 ApplicationContext 调用应收创建
- PurchaseReceiptServiceImpl.createReceipt() 中通过 ApplicationContext 调用应付创建
- 回调失败不影响主流程（try-catch 优雅降级）

### 3. 收付款核销方式

**决策：** 一条应收/应付记录支持多条收款/付款记录，累计金额 <= 应收/应付金额。

**状态计算：**
- paidAmount = 0 → 未收付(0)
- 0 < paidAmount < amount → 部分收付(1)
- paidAmount >= amount → 已收付(2)

### 4. 发票信息内嵌

**决策：** 发票信息（发票号、开票日期、开票状态）直接放在应收/应付单上，不拆独立表。

**理由：** 发票与应收/应付一对一，拆出来增加复杂度，内嵌字段足够。

### 5. 模块依赖

**决策：** erp-finance 依赖 erp-common，不依赖 erp-order/erp-purchase（避免循环依赖）。

**触发方式：** erp-order 和 erp-purchase 通过 ApplicationContext.getBean() + 反射调用 erp-finance 服务方法，与现有 IQC/OQC 集成模式一致。

## Risks / Trade-offs

**风险1：发货回滚但应收已创建**
→ 当前架构不支持分布式事务，通过异常捕获保证不阻塞主流程；后续可引入消息队列

**风险2：部分收付款的精度**
→ 使用 BigDecimal 存储金额，避免浮点精度问题

**风险3：发票信息只记录不校验**
→ 先做记录功能，后续可对接税务系统

## 数据库设计

```sql
-- 应收账款
CREATE TABLE ar_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  receivable_no VARCHAR(50) NOT NULL UNIQUE COMMENT '应收单号',
  delivery_id BIGINT NOT NULL COMMENT '关联发货单ID',
  order_id BIGINT COMMENT '关联销售订单ID',
  customer_id BIGINT COMMENT '客户ID',
  amount DECIMAL(12,2) NOT NULL COMMENT '应收金额',
  paid_amount DECIMAL(12,2) DEFAULT 0 COMMENT '已收金额',
  invoice_no VARCHAR(100) COMMENT '发票号',
  invoice_date DATE COMMENT '开票日期',
  invoice_status TINYINT DEFAULT 0 COMMENT '0=未开票 1=已开票',
  status TINYINT DEFAULT 0 COMMENT '0=未收 1=部分收 2=已收',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_delivery_id (delivery_id),
  INDEX idx_customer_id (customer_id),
  INDEX idx_receivable_no (receivable_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应收账款';

-- 收款记录
CREATE TABLE ar_payment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  payment_no VARCHAR(50) NOT NULL UNIQUE COMMENT '收款单号',
  ar_id BIGINT NOT NULL COMMENT '应收单ID',
  amount DECIMAL(12,2) NOT NULL COMMENT '收款金额',
  payment_method VARCHAR(50) COMMENT '收款方式：银行转账/现金/承兑汇票',
  payment_date DATE COMMENT '收款日期',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_ar_id (ar_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收款记录';

-- 应付账款
CREATE TABLE ap_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  payable_no VARCHAR(50) NOT NULL UNIQUE COMMENT '应付单号',
  receipt_id BIGINT NOT NULL COMMENT '关联入库单ID',
  purchase_order_id BIGINT COMMENT '关联采购单ID',
  supplier_id BIGINT COMMENT '供应商ID',
  amount DECIMAL(12,2) NOT NULL COMMENT '应付金额',
  paid_amount DECIMAL(12,2) DEFAULT 0 COMMENT '已付金额',
  invoice_no VARCHAR(100) COMMENT '发票号',
  invoice_date DATE COMMENT '来票日期',
  invoice_status TINYINT DEFAULT 0 COMMENT '0=未开票 1=已开票',
  status TINYINT DEFAULT 0 COMMENT '0=未付 1=部分付 2=已付',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_receipt_id (receipt_id),
  INDEX idx_supplier_id (supplier_id),
  INDEX idx_payable_no (payable_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应付账款';

-- 付款记录
CREATE TABLE ap_payment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  payment_no VARCHAR(50) NOT NULL UNIQUE COMMENT '付款单号',
  ap_id BIGINT NOT NULL COMMENT '应付单ID',
  amount DECIMAL(12,2) NOT NULL COMMENT '付款金额',
  payment_method VARCHAR(50) COMMENT '付款方式：银行转账/现金/承兑汇票',
  payment_date DATE COMMENT '付款日期',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_ap_id (ap_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='付款记录';
```

## ErrorCode 设计

```java
// 财务模块错误码 (6050-6059)
int AR_NOT_FOUND = 6050;
int AR_NO_EXISTS = 6051;
int AR_PAYMENT_EXCEEDED = 6052;
int AP_NOT_FOUND = 6053;
int AP_NO_EXISTS = 6054;
int AP_PAYMENT_EXCEEDED = 6055;
```

## 模块结构

```
erp-finance/
  pom.xml
  src/main/java/com/erp/finance/
    entity/
      ArRecord.java
      ArPayment.java
      ApRecord.java
      ApPayment.java
    dto/
      ArRecordDTO.java
      ArPaymentDTO.java
      ApRecordDTO.java
      ApPaymentDTO.java
    mapper/
      ArRecordMapper.java
      ArPaymentMapper.java
      ApRecordMapper.java
      ApPaymentMapper.java
    service/
      ArRecordService.java
      ApRecordService.java
      impl/
        ArRecordServiceImpl.java
        ApRecordServiceImpl.java
    controller/
      ArRecordController.java
      ApRecordController.java
```
