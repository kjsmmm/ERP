## ADDED Requirements

### Requirement: 报工记录CRUD
系统 SHALL 提供报工记录的创建、查询功能。

#### Scenario: 创建报工记录
- **WHEN** 操作员提交工单ID、工序序号、报工数量、实际工时
- **THEN** 系统创建报工记录，更新工单实际数量

#### Scenario: 按工单查询报工记录
- **WHEN** 用户查看工单详情
- **THEN** 系统返回该工单的所有报工记录列表

#### Scenario: 报工数量校验
- **WHEN** 报工数量小于等于0
- **THEN** 系统返回错误"报工数量必须大于0"

### Requirement: 报工自动更新工单实际数量
系统 SHALL 在报工时自动累加工单的实际产出数量。

#### Scenario: 报工累加实际数量
- **WHEN** 操作员对工单报工100件
- **THEN** 系统将工单的 actual_qty 增加100

#### Scenario: 多次报工累加
- **WHEN** 工单已报工50件，再次报工30件
- **THEN** 工单 actual_qty 为80件
