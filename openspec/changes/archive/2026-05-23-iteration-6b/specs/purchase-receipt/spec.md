## ADDED Requirements

### Requirement: 入库确认生成应付单
采购入库成功后，系统 SHALL 自动调用财务模块创建应付单。

#### Scenario: 入库自动生成应付
- **WHEN** 采购入库成功后
- **THEN** 系统调用财务模块自动创建应付单，金额为入库金额
- **AND** 财务模块不可用时不影响入库主流程
