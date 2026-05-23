# 迭代 4a：生产基础数据

## Why

生产管理是工厂 ERP 的核心模块。迭代 4a 是生产管理的第一期，建立基础数据层：车间、班组、设备、工艺路线。没有这些基础数据，后续的生产计划、工单、报工都无法运行。

## What Changes

新增 `erp-production` 后端模块，包含：

**基础数据管理：**
- 车间管理：工厂内的生产区域，含编码、名称、负责人
- 班组管理：车间内的生产团队，关联班组长
- 设备类型：标准化设备分类（切割机、折弯机、焊机等）
- 设备管理：具体设备台账，关联车间和设备类型，含状态和维护信息
- 工艺路线：产品级的生产工序模板，含标准工时和所需设备类型

**前端页面：**
- 车间管理页、班组管理页、设备类型页、设备管理页
- 产品详情中集成工艺路线配置

**数据库：**
- 新建 workshop、team、equipment_type、equipment、process_route、process_step 表

## Capabilities

- **workshop-management**: 车间 CRUD，含负责人和状态
- **team-management**: 班组 CRUD，关联车间和班组长
- **equipment-management**: 设备类型 + 设备 CRUD，关联车间
- **process-route**: 工艺路线和工序步骤管理，挂在产品上

## Impact

- 新增模块：`erp-production`
- 修改模块：`erp-boot`（添加依赖）、`erp-product`（产品详情关联工艺路线）
- 前端新增：5 个页面 + 路由
- 数据库：6 张新表
