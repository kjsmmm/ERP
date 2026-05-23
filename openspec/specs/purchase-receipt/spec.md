# purchase-receipt Specification

## Purpose
TBD - created by archiving change iteration-5a. Update Purpose after archive.
## Requirements
### Requirement: 采购入库
系统 SHALL 支持采购入库操作，关联采购单明细。

#### Scenario: 创建入库记录
- **WHEN** 用户对采购单明细执行入库操作（填写入库数量、仓库）
- **THEN** 系统创建入库记录，inspection_status 默认为 0（免检）

#### Scenario: 入库数量不能超过剩余数量
- **WHEN** 用户入库数量超过采购单明细的剩余未入库数量
- **THEN** 系统返回错误"入库数量超出采购数量"

#### Scenario: 入库自动更新库存
- **WHEN** 入库记录创建成功
- **THEN** 系统调用库存服务增加库存

#### Scenario: 入库自动更新采购单明细已入库数量
- **WHEN** 入库记录创建成功
- **THEN** 系统更新采购单明细的 received_qty

#### Scenario: 入库自动更新采购单状态
- **WHEN** 采购单明细全部入库完成
- **THEN** 系统自动将采购单状态改为已完成

### Requirement: 采购入库查询
系统 SHALL 支持入库记录查询。

#### Scenario: 按采购单查询入库记录
- **WHEN** 用户查看采购单的入库记录
- **THEN** 系统返回该采购单的所有入库明细

### Requirement: 采购入库需等待 IQC 检验通过
采购入库 SHALL 要求关联的来料检验单状态为"已完成"且检验结果为"合格"才能执行入库操作。

#### Scenario: IQC 检验合格后入库
- **WHEN** 用户创建采购入库单
- **THEN** 系统检查关联的来料检验单是否合格，合格才允许入库

#### Scenario: IQC 检验不合格时入库
- **WHEN** 用户尝试对检验不合格的采购单入库
- **THEN** 系统拒绝入库操作，提示"来料检验未通过，无法入库"

#### Scenario: 未检验时入库
- **WHEN** 用户尝试对未检验的采购单入库
- **THEN** 系统拒绝入库操作，提示"请先完成来料检验"

### Requirement: 入库确认生成应付单
采购入库成功后，系统 SHALL 自动调用财务模块创建应付单。

#### Scenario: 入库自动生成应付
- **WHEN** 采购入库成功后
- **THEN** 系统调用财务模块自动创建应付单，金额为入库金额
- **AND** 财务模块不可用时不影响入库主流程

