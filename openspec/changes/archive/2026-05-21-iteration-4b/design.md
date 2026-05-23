# 迭代 4b 技术设计

## Context

erp-production 模块已有基础数据：Workshop、Team、Equipment、ProcessRoute。现需新增生产计划和工单管理，打通从销售订单到车间生产的链路。

## Goals / Non-Goals

**Goals:**
- 生产计划：关联销售订单，管理计划数量和日期
- 工单：从计划拆分，快照工艺路线，状态流转管理
- 前端：生产计划和工单的 CRUD + 状态操作页面

**Non-Goals:**
- 自动排产（后续迭代）
- 报工和物料扣减（4c）
- 计划与库存的联动计算

## Decisions

### D1: 数据模型

```
SalesOrder (erp-order)
    │
    │ 1:N (order_id)
    ▼
ProductionPlan (erp-production)
    │
    │ 1:N (plan_id)
    ▼
WorkOrder (erp-production)
    │
    │ 快照 (创建时复制)
    ▼
WorkOrderStep (erp-production)
```

**ProductionPlan 表:**
- plan_code: 计划编码
- order_id: 关联销售订单（可为空，支持手动创建）
- product_id: 产品
- planned_qty: 计划数量
- start_date / end_date: 计划起止日期
- status: 0草稿 1已下达 2执行中 3已完成
- remark

**WorkOrder 表:**
- order_no: 工单编号
- plan_id: 关联生产计划
- product_id: 产品
- workshop_id: 车间
- route_id: 原始工艺路线ID（快照来源）
- route_name: 快照路线名称
- planned_qty: 计划数量
- actual_qty: 实际数量（报工时更新）
- status: 0已创建 1已下达 2生产中 3已完工 4已关闭
- start_date / end_date: 计划起止日期
- remark

**WorkOrderStep 表:**
- work_order_id: 工单ID
- step_no: 步骤序号
- step_name: 步骤名称
- standard_time: 标准工时
- equipment_type: 设备类型
- description: 说明

### D2: 工单快照工艺路线

创建工单时，从 ProcessRoute + ProcessStep 复制数据到 WorkOrder + WorkOrderStep。

```java
// 创建工单时
ProcessRoute route = processRouteService.getDetail(routeId);
workOrder.setRouteId(route.getId());
workOrder.setRouteName(route.getRouteName());
// ...保存workOrder后
for (ProcessStep step : route.getSteps()) {
    WorkOrderStep ws = new WorkOrderStep();
    ws.setWorkOrderId(workOrder.getId());
    ws.setStepNo(step.getStepNo());
    ws.setStepName(step.getStepName());
    // ...
}
```

### D3: 工单状态流转

```
已创建(0) ──下达──▶ 已下达(1) ──开工──▶ 生产中(2) ──完工──▶ 已完工(3) ──关闭──▶ 已关闭(4)
                   │                                            │
                   └──────────────────关闭──────────────────────┘
```

- 下达：计划确认后下发到车间
- 开工：车间开始生产
- 完工：生产完成，更新实际数量
- 关闭：归档，不可再操作

### D4: ErrorCode 扩展

```java
int PLAN_NOT_FOUND = 6010;
int PLAN_CODE_EXISTS = 6011;
int WORK_ORDER_NOT_FOUND = 6012;
int WORK_ORDER_NO_EXISTS = 6013;
int WORK_ORDER_STATUS_ERROR = 6014;
```
