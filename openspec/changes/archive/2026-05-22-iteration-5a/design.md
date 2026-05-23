# 迭代 5a 技术设计

## Context

生产管理模块（迭代4）已完成，需要新增采购管理模块。Flowable 审批基础设施已在迭代3搭建（WorkflowService + order-change-approval BPMN），可直接复用。

## Goals / Non-Goals

**Goals:**
- 供应商档案 CRUD
- 采购申请 + Flowable 审批
- 采购单（可引用申请）+ 状态管理
- 采购入库 + 库存更新
- 预留质检接口

**Non-Goals:**
- IQC 质检流程（5b）
- 采购对账
- 自动补货建议
- 多币种

## Decisions

### D1: 新建 erp-purchase 模块

遵循现有模块化单体架构，新建 `erp-purchase` 模块。

```
erp-purchase/
├── controller/
│   ├── SupplierController.java
│   ├── PurchaseRequestController.java
│   ├── PurchaseOrderController.java
│   └── PurchaseReceiptController.java
├── dto/
├── entity/
├── mapper/
└── service/
    └── impl/
```

依赖：erp-common, erp-system, erp-product, erp-inventory

### D2: 数据模型

```
Supplier (供应商)
    │
    │ 1:N (supplier_id)
    ▼
PurchaseOrder (采购单)
    │
    │ 1:N (order_id)
    ▼
PurchaseOrderItem (采购单明细)

PurchaseRequest (采购申请)
    │
    │ 可被引用 (request_id on PurchaseOrderItem)
    ▼
PurchaseOrder

PurchaseReceipt (采购入库)
    │
    │ 关联 (order_id + item_id)
    ▼
PurchaseOrder + PurchaseOrderItem
```

**Supplier 表:**
- supplier_code: 供应商编码（唯一）
- supplier_name: 供应商名称
- contact_name: 联系人
- phone: 电话
- address: 地址
- status: 0停用 1正常

**PurchaseRequest 表:**
- request_code: 申请编码（唯一）
- applicant_id: 申请人
- reason: 用途说明
- status: 0草稿 1审批中 2已通过 3已驳回 4已关闭
- process_instance_id: Flowable 流程实例ID

**PurchaseRequestItem 表:**
- request_id: 申请ID
- product_id: 物料ID
- quantity: 数量
- remark: 备注

**PurchaseOrder 表:**
- order_no: 采购单号（唯一）
- supplier_id: 供应商ID
- order_id: 关联销售订单（可选）
- plan_id: 关联生产计划（可选）
- status: 0草稿 1已提交 2已确认 3部分入库 4已完成
- total_amount: 总金额（计算字段）
- remark

**PurchaseOrderItem 表:**
- order_id: 采购单ID
- request_item_id: 关联申请明细（可选）
- product_id: 物料ID
- quantity: 数量
- unit_price: 单价
- received_qty: 已入库数量

**PurchaseReceipt 表:**
- receipt_no: 入库单号
- order_id: 采购单ID
- order_item_id: 采购单明细ID
- product_id: 物料ID
- warehouse_id: 仓库ID
- quantity: 入库数量
- inspection_status: 0免检 1待检 2合格 3不合格
- remark

### D3: 采购申请审批流程

复用 WorkflowService，新增 BPMN 流程 `purchase-request-approval`：

```
提交申请 → 主管审批 → 通过/驳回
```

审批通过后，申请状态改为"已通过"，可被采购单引用。

### D4: 采购入库与库存联动

入库时调用库存服务（跨模块，通过 ApplicationContext 获取 StockService）：
1. 创建入库记录
2. 调用 stockIn 更新库存
3. 更新采购单明细的 received_qty
4. 检查是否全部入库，自动更新采购单状态

### D5: ErrorCode 扩展

```java
int SUPPLIER_CODE_EXISTS = 6020;
int SUPPLIER_NOT_FOUND = 6021;
int PURCHASE_REQUEST_NOT_FOUND = 6022;
int PURCHASE_ORDER_NOT_FOUND = 6023;
int PURCHASE_ORDER_NO_EXISTS = 6024;
int PURCHASE_ORDER_STATUS_ERROR = 6025;
int RECEIPT_QTY_EXCEEDED = 6026;
```
