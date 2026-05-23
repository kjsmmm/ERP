# 迭代 3a 技术设计

## D1: 订单状态机

采用状态模式管理订单生命周期：

```
草稿(DRAFT) ──确认──▶ 已确认(CONFIRMED) ──开始生产──▶ 生产中(IN_PRODUCTION) ──完工──▶ 已完成(COMPLETED) ──关闭──▶ 已关闭(CLOSED)
   │                      │                                │
   ▼                      ▼                                ▼
 已取消(CANCELLED)     已取消(CANCELLED)                 已暂停(PAUSED)
```

状态流转规则：
- 草稿 → 已确认：校验库存可用量，冻结预留库存
- 已确认 → 已取消：释放预留库存
- 已确认 → 生产中：由生产模块触发（迭代 4）
- 生产中 → 已完成：由生产模块触发
- 已完成 → 已关闭：手动关闭
- 生产中 → 已暂停：手动暂停

状态存储为 Integer 枚举（1-7），服务层通过 Map<State, Set<State>> 校验流转合法性。迭代 3a 中已确认订单可直接编辑（不做审批），迭代 3b 引入 Flowable 后改为变更审批。

## D2: 库存预留模型

采用 `on_hand_qty` + `reserved_qty` 双字段模型：

```
available_qty = on_hand_qty - reserved_qty
```

| 操作 | on_hand | reserved | 场景 |
|------|---------|----------|------|
| 入库 | +N | 不变 | 采购入库、生产入库 |
| 出库 | -N | -N | 销售出库、生产领料 |
| 预留 | 不变 | +N | 订单确认 |
| 释放 | 不变 | -N | 订单取消 |

## D3: 订单号生成

格式：`SO-YYYYMMDD-NNN`，查询当日最大序号 +1，与客户编码生成逻辑一致。

## D4: 模块结构

```
erp-order/
├── controller/    OrderController
├── service/       OrderService
│   └── impl/
├── mapper/        OrderMapper, OrderItemMapper
├── entity/        SalesOrder, OrderItem
├── dto/           OrderDTO, OrderItemDTO, OrderQueryDTO
└── vo/            OrderDetailVO

erp-inventory/
├── controller/    WarehouseController, InventoryController, StockRecordController
├── service/       WarehouseService, InventoryService, StockService
│   └── impl/
├── mapper/        WarehouseMapper, InventoryMapper, StockRecordMapper
├── entity/        Warehouse, Inventory, StockRecord
├── dto/           WarehouseDTO, StockInDTO, StockOutDTO, InventoryQueryDTO
└── vo/            InventoryVO
```

## D5: 前端页面

```
erp-frontend/src/views/
├── order/
│   ├── list/          订单列表
│   ├── detail/        订单详情
│   └── components/    订单表单、明细表格
└── inventory/
    ├── warehouse/     仓库管理
    ├── stock/         库存查询
    ├── inbound/       入库管理
    └── outbound/      出库管理
```
