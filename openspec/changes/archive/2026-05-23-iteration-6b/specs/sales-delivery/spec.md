## ADDED Requirements

### Requirement: 出库确认
系统 SHALL 在发货出库确认时自动扣减库存并创建应收单。

#### Scenario: 出库确认扣减库存
- **WHEN** 用户确认发货出库
- **THEN** 系统扣减对应仓库的产品库存

#### Scenario: 出库确认生成应收
- **WHEN** 发货出库确认成功
- **THEN** 系统调用财务模块自动创建应收单，金额为发货金额
- **AND** 财务模块不可用时不影响出库主流程
