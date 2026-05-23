## 1. 基础设施

- [x] 1.1 ErrorCode 添加销售发货/退货错误码（6040-6049）

## 2. 销售发货

- [x] 2.1 创建 SalesDelivery / SalesDeliveryItem 实体 + DTO + Mapper
- [x] 2.2 实现 SalesDeliveryService（创建发货单 + 状态流转 + 出库扣库存）
- [x] 2.3 创建 SalesDeliveryController
- [x] 2.4 创建发货管理前端页面

## 3. 销售退货

- [x] 3.1 创建 SalesReturn / SalesReturnItem 实体 + DTO + Mapper
- [x] 3.2 创建 sales-return-approval.bpmn20.xml 审批流程
- [x] 3.3 实现 SalesReturnService（创建退货 + 提交审批 + 审批回调 + 入库加库存）
- [x] 3.4 创建 SalesReturnController
- [x] 3.5 创建退货管理前端页面

## 4. 订单集成

- [x] 4.1 修改 OrderService/OrderController，订单详情返回发货状态

## 5. 数据库与路由

- [x] 5.1 创建 iteration-6a.sql 建表 SQL
- [x] 5.2 前端路由配置（销售发货、退货菜单）
