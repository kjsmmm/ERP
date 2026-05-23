## ADDED Requirements

### Requirement: 工单CRUD
系统 SHALL 提供工单的创建、查询、编辑、删除功能。

#### Scenario: 创建工单
- **WHEN** 用户提交工单信息（关联计划、车间、工艺路线）
- **THEN** 系统创建工单，快照工艺路线到 WorkOrderStep，状态为已创建

#### Scenario: 工单编号唯一性校验
- **WHEN** 用户创建工单时编号已存在
- **THEN** 系统返回错误"工单编号已存在"

#### Scenario: 分页查询工单列表
- **WHEN** 用户查询工单列表，可按车间、状态筛选
- **THEN** 系统返回分页数据，包含车间名称、路线名称

#### Scenario: 查看工单详情
- **WHEN** 用户查看工单详情
- **THEN** 系统返回工单信息及快照的工序步骤列表

#### Scenario: 删除已创建状态的工单
- **WHEN** 用户删除状态为已创建的工单
- **THEN** 系统删除工单及关联的步骤快照

### Requirement: 工单工艺路线快照
系统 SHALL 在创建工单时复制工艺路线数据作为快照。

#### Scenario: 创建工单时快照工艺路线
- **WHEN** 用户创建工单并指定工艺路线
- **THEN** 系统从 ProcessRoute + ProcessStep 复制数据到 WorkOrder + WorkOrderStep

#### Scenario: 快照独立于原始路线
- **WHEN** 原始工艺路线被修改
- **THEN** 工单的快照步骤不受影响

### Requirement: 工单状态流转
系统 SHALL 支持工单状态流转：已创建 → 已下达 → 生产中 → 已完工 → 已关闭。

#### Scenario: 下达工单
- **WHEN** 用户下达已创建状态的工单
- **THEN** 系统将状态改为"已下达"

#### Scenario: 开始生产
- **WHEN** 用户将已下达的工单标记为生产中
- **THEN** 系统将状态改为"生产中"

#### Scenario: 完工
- **WHEN** 用户将生产中的工单标记完工，提交实际数量
- **THEN** 系统将状态改为"已完工"，更新 actual_qty

#### Scenario: 关闭工单
- **WHEN** 用户关闭已完工的工单
- **THEN** 系统将状态改为"已关闭"

#### Scenario: 非法状态转换
- **WHEN** 用户尝试非法的状态转换（如从已创建直接到生产中）
- **THEN** 系统返回错误"工单状态不允许该操作"
