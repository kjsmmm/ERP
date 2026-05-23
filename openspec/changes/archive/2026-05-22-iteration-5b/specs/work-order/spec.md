## MODIFIED Requirements

### Requirement: 工单完成时触发 OQC 检验
工单完成 SHALL 自动触发创建成品检验单，质检员需要完成检验后产品才能入成品库。

#### Scenario: 工单完成触发检验
- **WHEN** 工单状态变为"已完成"
- **THEN** 系统自动创建成品检验单，状态为"待检验"

#### Scenario: OQC 检验合格
- **WHEN** 成品检验单检验结果为合格
- **THEN** 系统允许将产品入成品库

#### Scenario: OQC 检验不合格
- **WHEN** 成品检验单检验结果为不合格
- **THEN** 系统触发不合格品处理流程，产品不能入成品库
