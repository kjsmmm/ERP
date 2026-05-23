## ADDED Requirements

### Requirement: 完工时物料扣减
系统 SHALL 在工单完工时按实际产量扣减物料库存。

#### Scenario: 正常扣减
- **WHEN** 工单完工，实际产量100，BOM中某物料单位用量2、损耗率5%
- **THEN** 系统扣减该物料 100 × 2 × 1.05 = 210 件

#### Scenario: 库存不足阻止完工
- **WHEN** 工单完工时某物料库存不足
- **THEN** 系统返回错误"库存不足"，阻止完工

#### Scenario: 无BOM的产品
- **WHEN** 工单产品没有BOM
- **THEN** 系统跳过物料扣减，正常完工

### Requirement: 成品自动入库
系统 SHALL 在工单完工后自动将成品入库。

#### Scenario: 自动入库
- **WHEN** 工单完工，实际产量100
- **THEN** 系统在默认仓库创建100件成品的入库记录

#### Scenario: 入库来源追溯
- **WHEN** 成品自动入库
- **THEN** 入库记录的来源单号为工单编号
