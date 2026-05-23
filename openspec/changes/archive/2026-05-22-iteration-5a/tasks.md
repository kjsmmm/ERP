## 1. erp-purchase 模块搭建

- [x] 1.1 创建 erp-purchase 模块目录结构 + pom.xml
- [x] 1.2 erp-boot pom.xml 添加 erp-purchase 依赖
- [x] 1.3 application.yml type-aliases-package 添加 com.erp.purchase.entity
- [x] 1.4 ErrorCode 添加采购模块错误码（6020-6029）

## 2. 供应商管理

- [x] 2.1 创建 Supplier 实体 / SupplierDTO / SupplierMapper
- [x] 2.2 实现 SupplierService（CRUD + 编码唯一校验 + 删除检查引用）
- [x] 2.3 创建 SupplierController
- [x] 2.4 创建供应商管理前端页面

## 3. 采购申请

- [x] 3.1 创建 PurchaseRequest / PurchaseRequestItem 实体 + DTO + Mapper
- [x] 3.2 创建 purchase-request-approval.bpmn20.xml 审批流程
- [x] 3.3 实现 PurchaseRequestService（CRUD + 提交审批 + 审批回调）
- [x] 3.4 创建 PurchaseRequestController
- [x] 3.5 创建采购申请前端页面

## 4. 采购单

- [x] 4.1 创建 PurchaseOrder / PurchaseOrderItem 实体 + DTO + Mapper
- [x] 4.2 实现 PurchaseOrderService（CRUD + 引用申请 + 状态流转）
- [x] 4.3 创建 PurchaseOrderController
- [x] 4.4 创建采购单前端页面

## 5. 采购入库

- [x] 5.1 创建 PurchaseReceipt 实体 + DTO + Mapper
- [x] 5.2 实现 PurchaseReceiptService（入库 + 更新库存 + 更新采购单状态）
- [x] 5.3 创建 PurchaseReceiptController
- [x] 5.4 创建采购入库前端页面

## 6. 数据库与路由

- [x] 6.1 创建采购模块数据库建表 SQL
- [x] 6.2 前端路由配置（采购管理菜单）
