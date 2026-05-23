# purchase-request Specification

## Purpose
TBD - created by archiving change iteration-5a. Update Purpose after archive.
## Requirements
### Requirement: 采购申请CRUD
系统 SHALL 提供采购申请的创建、查询、查看详情功能。

#### Scenario: 创建采购申请
- **WHEN** 用户提交采购申请（物料列表、数量、用途说明）
- **THEN** 系统创建申请，状态为草稿

#### Scenario: 查询采购申请列表
- **WHEN** 用户查询采购申请
- **THEN** 系统返回分页数据，显示申请编码、申请人、状态

#### Scenario: 查看采购申请详情
- **WHEN** 用户查看申请详情
- **THEN** 系统返回申请信息及物料明细列表

### Requirement: 采购申请审批
系统 SHALL 支持采购申请通过 Flowable 审批。

#### Scenario: 提交审批
- **WHEN** 用户提交草稿状态的申请进入审批
- **THEN** 系统启动 Flowable 流程，状态改为审批中

#### Scenario: 审批通过
- **WHEN** 主管审批通过
- **THEN** 系统将状态改为已通过

#### Scenario: 审批驳回
- **WHEN** 主管驳回申请
- **THEN** 系统将状态改为已驳回

#### Scenario: 已通过的申请可被采购单引用
- **WHEN** 采购员创建采购单时
- **THEN** 系统显示已通过的申请列表供引用

