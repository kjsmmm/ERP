## Context

ERP系统已完成采购管理(iteration-5a)和生产管理(iteration-4a/4b/4c)，但缺少质量检验环节。当前采购入库和工单完成都没有质量检验流程，需要新增 IQC/OQC 检验和不合格品处理。

**现有模块：**
- erp-purchase：采购申请、采购单、采购入库
- erp-production：工单、报工
- erp-common：WorkflowService（Flowable 审批）

## Goals / Non-Goals

**Goals:**
- 实现来料检验(IQC)，采购入库前必须检验合格
- 实现成品检验(OQC)，工单完成时自动触发检验
- 实现不合格品处理流程，带 Flowable 审批
- 检验标准可按产品分类复用

**Non-Goals:**
- 不实现过程检验(IPQC)，留到后续迭代
- 不实现复杂的质量统计报表，先做基础查询
- 不实现检验设备集成（手动填写检验数据）

## Decisions

### 1. 模块结构：新建 erp-quality 模块

**决策：** 新建独立的 erp-quality 模块，不放在 erp-purchase 或 erp-production 中。

**理由：**
- 质量管理是独立的业务域，横跨采购和生产
- 后续可扩展 IPQC、质量统计等功能
- 模块边界清晰，符合现有架构风格

**替代方案：**
- 在 erp-purchase 中扩展 IQC，在 erp-production 中扩展 OQC → 跨模块耦合，不推荐

### 2. 数据模型：检验标准与检验单分离

**决策：**
- quality_standard + quality_standard_item：检验标准模板
- iq_inspection + iq_inspection_item：来料检验单（从标准复制）
- oq_inspection + oq_inspection_item：成品检验单（从标准复制）

**理由：**
- 标准是模板，检验单是实例，修改标准不影响历史检验记录
- 检验单的检验项目从标准复制，可独立修改

### 3. 不合格品处理：Flowable 审批

**决策：** 使用 Flowable 审批流程，BPMN 流程与采购申请类似。

**理由：**
- 保持与现有审批流程一致
- 后续可扩展审批链（如金额大的需要厂长审批）

### 4. IQC 与采购入库的衔接

**决策：** 采购入库时检查关联的 IQC 检验单是否合格。

**实现：**
- iq_inspection 表增加 purchase_order_id 字段
- PurchaseReceiptServiceImpl 创建入库单时检查 IQC 状态
- IQC 未通过则拒绝入库

### 5. OQC 与工单的衔接

**决策：** 工单完成时自动创建 OQC 检验单。

**实现：**
- WorkOrderServiceImpl.complete() 方法中创建 OQC 检验单
- oq_inspection 表增加 work_order_id 字段
- OQC 合格后才能入成品库

## Risks / Trade-offs

**风险1：检验单与采购单/工单的数据一致性**
→ 通过事务保证，检验单创建失败则回滚采购入库/工单完成操作

**风险2：检验标准修改后，已创建的检验单是否受影响**
→ 检验单从标准复制项目，修改标准不影响已创建的检验单

**风险3：不合格品处理的审批流程复杂度**
→ 初期简化为单级审批（质量主管），后续可扩展

## 数据库设计

```sql
-- 检验标准
CREATE TABLE quality_standard (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  standard_code VARCHAR(50) NOT NULL UNIQUE,
  standard_name VARCHAR(100) NOT NULL,
  applicable_type TINYINT COMMENT '1=原材料 2=半成品 3=成品',
  category_id BIGINT COMMENT '关联产品分类，为空表示通用',
  status TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 检验项目
CREATE TABLE quality_standard_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  standard_id BIGINT NOT NULL,
  item_name VARCHAR(100) NOT NULL,
  inspection_method VARCHAR(200),
  standard_value VARCHAR(200),
  judgment_rule VARCHAR(200),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_standard_id (standard_id)
);

-- 来料检验
CREATE TABLE iq_inspection (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  inspection_no VARCHAR(50) NOT NULL UNIQUE,
  purchase_order_id BIGINT NOT NULL,
  supplier_id BIGINT,
  inspection_result TINYINT COMMENT '0=待检验 1=合格 2=不合格',
  status TINYINT DEFAULT 0 COMMENT '0=待检验 1=检验中 2=已完成',
  inspector_id BIGINT,
  inspection_time DATETIME,
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_order_id (purchase_order_id),
  INDEX idx_inspection_no (inspection_no)
);

-- 来料检验项目
CREATE TABLE iq_inspection_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  iq_inspection_id BIGINT NOT NULL,
  item_name VARCHAR(100),
  inspection_method VARCHAR(200),
  standard_value VARCHAR(200),
  actual_value VARCHAR(200),
  judgment TINYINT COMMENT '0=未判定 1=合格 2=不合格',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_inspection_id (iq_inspection_id)
);

-- 成品检验
CREATE TABLE oq_inspection (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  inspection_no VARCHAR(50) NOT NULL UNIQUE,
  work_order_id BIGINT NOT NULL,
  product_id BIGINT,
  quantity INT,
  inspection_result TINYINT COMMENT '0=待检验 1=合格 2=不合格',
  status TINYINT DEFAULT 0 COMMENT '0=待检验 1=检验中 2=已完成',
  inspector_id BIGINT,
  inspection_time DATETIME,
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_work_order_id (work_order_id),
  INDEX idx_inspection_no (inspection_no)
);

-- 成品检验项目
CREATE TABLE oq_inspection_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  oq_inspection_id BIGINT NOT NULL,
  item_name VARCHAR(100),
  inspection_method VARCHAR(200),
  standard_value VARCHAR(200),
  actual_value VARCHAR(200),
  judgment TINYINT COMMENT '0=未判定 1=合格 2=不合格',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_inspection_id (oq_inspection_id)
);

-- 不合格品处理
CREATE TABLE defect_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  record_no VARCHAR(50) NOT NULL UNIQUE,
  source_type TINYINT COMMENT '1=IQC 2=OQC',
  source_id BIGINT COMMENT '检验单ID',
  product_id BIGINT,
  quantity INT,
  defect_reason VARCHAR(500),
  handle_type TINYINT COMMENT '1=退货 2=返工 3=报废 4=让步接收',
  handle_remark VARCHAR(500),
  status TINYINT DEFAULT 0 COMMENT '0=待审批 1=审批中 2=已通过 3=已驳回 4=已处理',
  process_instance_id VARCHAR(100),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_record_no (record_no),
  INDEX idx_source (source_type, source_id)
);
```

## ErrorCode 设计

```java
// 质量模块错误码 (6030-6039)
int STANDARD_CODE_EXISTS = 6030;
int STANDARD_NOT_FOUND = 6031;
int IQ_INSPECTION_NOT_FOUND = 6032;
int OQ_INSPECTION_NOT_FOUND = 6033;
int DEFECT_RECORD_NOT_FOUND = 6034;
int INSPECTION_NOT_COMPLETED = 6035;
int INSPECTION_RESULT_NOT合格 = 6036;
```

## BPMN 流程设计

**defect-handling-approval.bpmn20.xml：**
```
提交不合格品处理 → 质量主管审批 → 结束
                    ├─ 通过 → 执行处理
                    └─ 驳回 → 重新提交
```
