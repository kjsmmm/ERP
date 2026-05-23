## 1. 订单管理模块

- [x] 1.1 创建 erp-order 模块目录结构 + pom.xml
- [x] 1.2 创建 SalesOrder 实体（订单主表）
- [x] 1.3 创建 OrderItem 实体（订单明细表）
- [x] 1.4 创建 OrderDTO / OrderItemDTO / OrderQueryDTO
- [x] 1.5 创建 OrderDetailVO（订单信息 + 明细 + 客户信息）
- [x] 1.6 创建 OrderMapper / OrderItemMapper
- [x] 1.7 实现 OrderService：创建订单（含明细、自动计算金额）
- [x] 1.8 实现 OrderService：订单列表分页查询（客户名、状态、日期范围筛选）
- [x] 1.9 实现 OrderService：订单详情（主表 + 明细 + 客户信息）
- [x] 1.10 实现 OrderService：订单状态机（校验流转合法性 + 库存预留/释放联动）
- [x] 1.11 实现 OrderService：订单编辑（草稿/已确认状态可改）
- [x] 1.12 实现 OrderService：订单删除（仅草稿状态可删）
- [x] 1.13 创建 OrderController（订单 CRUD + 状态变更）
- [x] 1.14 erp-boot 添加 erp-order 模块依赖
- [x] 1.15 创建订单相关数据库建表 SQL

## 2. 库存管理模块

- [x] 2.1 创建 erp-inventory 模块目录结构 + pom.xml
- [x] 2.2 创建 Warehouse 实体（仓库表）
- [x] 2.3 创建 Inventory 实体（库存表：product_id, warehouse_id, on_hand_qty, reserved_qty）
- [x] 2.4 创建 StockRecord 实体（库存流水表）
- [x] 2.5 创建 WarehouseDTO / StockInDTO / StockOutDTO / InventoryQueryDTO
- [x] 2.6 创建 InventoryVO（含 available_qty 动态计算）
- [x] 2.7 创建 WarehouseMapper / InventoryMapper / StockRecordMapper
- [x] 2.8 实现 WarehouseService：仓库 CRUD
- [x] 2.9 实现 InventoryService：库存查询（按产品/仓库，显示实物量/预留量/可用量）
- [x] 2.10 实现 StockService：入库操作（增加 on_hand_qty + 写流水）
- [x] 2.11 实现 StockService：出库操作（减少 on_hand_qty + 写流水，校验库存充足）
- [x] 2.12 实现 StockService：预留操作（增加 reserved_qty，校验 available_qty 足够）
- [x] 2.13 实现 StockService：释放操作（减少 reserved_qty）
- [x] 2.14 创建 WarehouseController / InventoryController / StockRecordController
- [x] 2.15 erp-boot 添加 erp-inventory 模块依赖
- [x] 2.16 创建库存相关数据库建表 SQL

## 3. 前端页面

- [x] 3.1 订单列表页：搜索（客户名/订单号/状态/日期）、分页、新建入口
- [x] 3.2 订单创建/编辑表单：选客户 → 选产品 → 填数量/单价
- [x] 3.3 订单详情页：订单信息 + 明细表格 + 状态流转按钮
- [x] 3.4 仓库管理页：仓库 CRUD
- [x] 3.5 库存查询页：按产品/仓库筛选，显示实物量/预留量/可用量
- [x] 3.6 入库管理页：选择仓库+产品+数量，执行入库
- [x] 3.7 出库管理页：选择仓库+产品+数量，执行出库（校验可用量）

## 4. 联调测试

- [x] 4.1 订单创建 → 库存预留 → 订单取消 → 库存释放 全流程验证
- [x] 4.2 入库/出库 → 库存数量变化 验证
