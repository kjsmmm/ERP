# defect-handling Specification

## Purpose
TBD - created by archiving change iteration-5b. Update Purpose after archive.
## Requirements
### Requirement: 创建不合格品记录
系统 SHALL 支持创建不合格品记录，关联检验单（IQC或OQC），记录不合格数量和原因。

#### Scenario: 从检验单创建不合格品记录
- **WHEN** 检验结果为不合格时
- **THEN** 系统支持创建不合格品记录，自动关联检验单信息

### Requirement: 选择处理方式
不合格品记录 SHALL 支持选择处理方式：退货、返工、报废、让步接收。

#### Scenario: 选择退货处理
- **WHEN** 用户选择"退货"处理方式
- **THEN** 系统记录处理方式为退货，等待审批

#### Scenario: 选择返工处理
- **WHEN** 用户选择"返工"处理方式
- **THEN** 系统记录处理方式为返工，等待审批

#### Scenario: 选择报废处理
- **WHEN** 用户选择"报废"处理方式
- **THEN** 系统记录处理方式为报废，等待审批

#### Scenario: 选择让步接收
- **WHEN** 用户选择"让步接收"处理方式
- **THEN** 系统记录处理方式为让步接收，等待审批

### Requirement: 提交审批
不合格品处理 SHALL 支持提交 Flowable 审批流程。

#### Scenario: 提交审批
- **WHEN** 用户填写处理意见并提交审批
- **THEN** 系统启动审批流程，状态变为"审批中"

### Requirement: 审批通过后执行处理
系统 SHALL 在审批通过后执行相应的处理操作。

#### Scenario: 审批通过-退货
- **WHEN** 退货处理审批通过
- **THEN** 系统更新采购单状态，标记退货

#### Scenario: 审批通过-返工
- **WHEN** 返工处理审批通过
- **THEN** 系统创建返工工单

#### Scenario: 审批通过-报废
- **WHEN** 报废处理审批通过
- **THEN** 系统核销库存，记录报废数量

#### Scenario: 审批通过-让步接收
- **WHEN** 让步接收审批通过
- **THEN** 系统允许入库，但标记为让步接收

### Requirement: 审批驳回
系统 SHALL 支持审批驳回，允许重新提交。

#### Scenario: 审批驳回
- **WHEN** 主管驳回不合格品处理申请
- **THEN** 系统将状态更新为"已驳回"，用户可修改后重新提交

### Requirement: 不合格品记录分页查询
系统 SHALL 支持按记录编号、处理方式、状态分页查询。

#### Scenario: 按处理方式筛选
- **WHEN** 用户选择"报废"筛选条件
- **THEN** 系统返回所有报废处理的不合格品记录

