# 迭代 4c 技术设计

## Context

工单（4b）已支持创建、下达、开工。现需实现报工记录实际产出，完工时自动扣减物料并入库。

## Goals / Non-Goals

**Goals:**
- 报工：按工单记录每道工序的实际产出和工时
- 物料扣减：完工时按实际产量 × BOM 扣减库存
- 自动入库：完工后成品自动入库

**Non-Goals:**
- 报工审批流程
- 工序级物料扣减（只在完工时统一扣减）
- 多仓库分配策略

## Decisions

### D1: 报工数据模型

```
WorkReport 表:
- work_order_id: 工单ID
- step_no: 工序序号
- step_name: 工序名称
- report_qty: 报工数量
- actual_hours: 实际工时(分钟)
- report_time: 报工时间
- reporter_id: 报工人
- remark
```

一个工单可以有多条报工记录（不同工序、不同批次）。
工单的 actual_qty = SUM(所有报工的 report_qty)。

### D2: 物料扣减逻辑

工单完工时执行：

```
1. 获取工单的产品ID和实际产量
2. 获取该产品的BOM子项列表
3. 对每个BOM子项：
   扣减数量 = actual_qty × quantity × (1 + wasteRate/100)
4. 从库存中扣减（默认仓库）
5. 库存不足时抛出异常，阻止完工
6. 成品自动入库到默认仓库
```

集成点：修改 WorkOrderServiceImpl.complete() 方法，注入 BomItemMapper 和库存服务。

### D3: 自动入库

完工后调用库存服务创建入库记录：
- 产品：工单的产品
- 数量：实际产出
- 仓库：默认仓库（第一个启用的仓库）
- 来源单号：工单编号

### D4: ErrorCode 扩展

```java
int STOCK_INSUFFICIENT = 6016;
```
