## 1. 基础设施

- [x] 1.1 创建 erp-finance 模块（pom.xml + 包结构）
- [x] 1.2 ErrorCode 添加财务模块错误码（6050-6059）
- [x] 1.3 erp-boot pom.xml 添加 erp-finance 依赖

## 2. 应收账款 (AR)

- [x] 2.1 创建 ArRecord / ArPayment 实体 + DTO + Mapper
- [x] 2.2 实现 ArRecordService（应收查询 + 发票登记 + 收款核销）
- [x] 2.3 创建 ArRecordController
- [x] 2.4 创建应收管理前端页面

## 3. 应付账款 (AP)

- [x] 3.1 创建 ApRecord / ApPayment 实体 + DTO + Mapper
- [x] 3.2 实现 ApRecordService（应付查询 + 发票登记 + 付款核销）
- [x] 3.3 创建 ApRecordController
- [x] 3.4 创建应付管理前端页面

## 4. 业务联动

- [x] 4.1 修改 SalesDeliveryServiceImpl.shipOut()，出库后自动创建应收单
- [x] 4.2 修改 PurchaseReceiptServiceImpl.createReceipt()，入库后自动创建应付单

## 5. 数据库与路由

- [x] 5.1 创建 iteration-6b.sql 建表 SQL
- [x] 5.2 前端路由配置（应收管理、应付管理菜单）
