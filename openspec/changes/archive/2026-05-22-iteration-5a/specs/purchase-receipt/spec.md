## ADDED Requirements

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
