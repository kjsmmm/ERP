# accounts-receivable Specification

## Purpose
TBD - created by archiving change iteration-6b. Update Purpose after archive.
## Requirements
### Requirement: 应收单自动生成
系统 SHALL 在销售发货出库确认后自动创建应收单，记录应收金额、关联发货单和客户。

#### Scenario: 发货出库自动生成应收
- **WHEN** 销售发货单完成出库确认
- **THEN** 系统自动创建应收单，金额等于发货金额，状态为"未收"

#### Scenario: 发货金额即为应收金额
- **WHEN** 发货单包含多个产品明细
- **THEN** 应收金额等于各明细数量 × 单价的合计

### Requirement: 应收单查询
系统 SHALL 支持按客户、状态、日期范围分页查询应收单列表。

#### Scenario: 按客户查询应收
- **WHEN** 用户按客户筛选应收单
- **THEN** 系统返回该客户的所有应收记录

#### Scenario: 按状态查询应收
- **WHEN** 用户按状态筛选（未收/部分收/已收）
- **THEN** 系统返回对应状态的应收记录

### Requirement: 发票登记
系统 SHALL 支持在应收单上登记发票号、开票日期和开票状态。

#### Scenario: 登记发票信息
- **WHEN** 用户对应收单录入发票号和开票日期
- **THEN** 系统更新应收单的发票信息，开票状态变为"已开票"

### Requirement: 收款核销
系统 SHALL 支持对应收单进行多次部分收款，累计收款金额不超过应收金额。

#### Scenario: 首次部分收款
- **WHEN** 用户录入收款金额（小于应收金额）
- **THEN** 系统创建收款记录，更新应收单已收金额，状态变为"部分收"

#### Scenario: 累计收款达到应收金额
- **WHEN** 累计收款金额等于应收金额
- **THEN** 系统状态变为"已收"

#### Scenario: 收款金额超出应收
- **WHEN** 用户录入的收款金额使累计收款超出应收金额
- **THEN** 系统拒绝操作，提示"收款金额超出应收金额"

