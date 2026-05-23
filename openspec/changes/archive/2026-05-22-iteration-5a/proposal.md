# 迭代 5a: 采购管理

## Why

生产管理（迭代4）已完成，工厂核心业务链路只剩采购和质量两个模块。采购管理是供应链保障的关键环节——物料不足时需要向供应商下单采购，到货后入库补充库存。

## What Changes

### 供应商管理 (Supplier)
- 供应商档案 CRUD（编码、名称、联系人、电话、地址、状态）
- 作为采购单的基础数据

### 采购申请 (PurchaseRequest)
- 提交采购需求（物料、数量、用途说明）
- 走 Flowable 审批流（复用已有的 WorkflowService）
- 审批通过后可被采购单引用

### 采购单 (PurchaseOrder)
- 手动创建，可引用已审批的采购申请自动带出明细
- 关联供应商，填写单价和交期
- 状态流转：草稿 → 已提交 → 已确认 → 部分入库 → 已完成
- 可选关联销售订单或生产计划（追溯链）

### 采购入库 (PurchaseReceipt)
- 关联采购单，记录到货数量
- 预留 `inspectionStatus` 字段（0免检 1待检），5b 质检模块对接
- 入库时更新库存

## Capabilities

- **supplier**: 供应商档案管理
- **purchase-request**: 采购申请 + Flowable 审批
- **purchase-order**: 采购单（可引用申请）+ 状态管理
- **purchase-receipt**: 采购入库 + 库存更新

## Impact

- 新增 erp-purchase 模块（或在 erp-production 同级新建）
- 前端新增供应商、采购申请、采购单、采购入库页面
- 数据库新增 supplier、purchase_request、purchase_order、purchase_order_item、purchase_receipt 五张表
- Flowable 新增采购审批 BPMN 流程
