# purchase-order Specification

## Purpose
TBD - created by archiving change iteration-5a. Update Purpose after archive.
## Requirements
### Requirement: 采购单CRUD
系统 SHALL 提供采购单的创建、查询、编辑、删除功能。

#### Scenario: 创建采购单
- **WHEN** 用户提交采购单信息（供应商、明细列表、单价、交期）
- **THEN** 系统创建采购单，状态为草稿

#### Scenario: 创建采购单时引用采购申请
- **WHEN** 用户创建采购单时选择引用已审批的采购申请
- **THEN** 系统自动带出申请的物料和数量到采购单明细

#### Scenario: 采购单编号唯一性校验
- **WHEN** 用户创建采购单时编号已存在
- **THEN** 系统返回错误"采购单编号已存在"

#### Scenario: 分页查询采购单列表
- **WHEN** 用户查询采购单列表，可按供应商、状态筛选
- **THEN** 系统返回分页数据

#### Scenario: 查看采购单详情
- **WHEN** 用户查看采购单详情
- **THEN** 系统返回采购单信息及明细列表，包含物料名称、已入库数量

#### Scenario: 删除草稿状态的采购单
- **WHEN** 用户删除草稿状态的采购单
- **THEN** 系统删除采购单及明细

### Requirement: 采购单状态管理
系统 SHALL 支持采购单状态流转。

#### Scenario: 提交采购单
- **WHEN** 用户提交草稿状态的采购单
- **THEN** 系统将状态改为已提交

#### Scenario: 确认采购单
- **WHEN** 用户确认已提交的采购单
- **THEN** 系统将状态改为已确认

#### Scenario: 部分入库自动更新状态
- **WHEN** 采购单部分明细已入库
- **THEN** 系统将状态改为部分入库

#### Scenario: 全部入库自动完成
- **WHEN** 采购单所有明细全部入库
- **THEN** 系统将状态改为已完成

