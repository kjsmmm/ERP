## Why

销售订单管理已完成，但缺少发货和退货环节。当前订单确认后无法跟踪发货状态，客户退货也没有规范流程。需要补全销售闭环：订单 → 发货 → 退货。

## What Changes

- 新增销售发货单管理，支持部分发货（一个订单可多次发货）
- 发货确认时自动扣减库存（跨模块调用 InventoryService）
- 新增销售退货单管理，关联发货单
- 退货使用 Flowable 审批流，审批通过后自动增加库存
- 录入物流公司和运单号

## Capabilities

### New Capabilities

- `sales-delivery`: 销售发货管理，包括发货单创建、拣货、出库确认、签收，支持部分发货，出库自动扣减库存
- `sales-return`: 销售退货管理，包括退货申请、Flowable 审批、退货入库，关联发货单，入库自动增加库存

### Modified Capabilities

- `order-management`: 销售订单增加发货状态跟踪（已发货数量、是否全部发货）

## Impact

- **后端模块**: erp-order（新增发货/退货实体、服务、控制器）
- **前端模块**: erp-frontend（新增发货管理、退货管理页面）
- **跨模块依赖**: 库存服务（出库扣减、入库增加），通过 ApplicationContext 调用
- **审批流程**: 新增退货审批 BPMN 流程
- **数据库**: 新增 sales_delivery、sales_delivery_item、sales_return、sales_return_item 表
