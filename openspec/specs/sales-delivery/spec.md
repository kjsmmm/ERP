# sales-delivery Specification

## Purpose
TBD - created by archiving change iteration-6a. Update Purpose after archive.
## Requirements
### Requirement: 创建发货单
系统 SHALL 支持创建销售发货单，关联销售订单，支持部分发货。发货单号 SHALL 自动生成，格式为 DLV-YYYYMMDD-NNN。

#### Scenario: 创建发货单
- **WHEN** 用户选择销售订单并填写发货明细（产品、数量、仓库）
- **THEN** 系统创建发货单，状态为草稿，自动关联客户信息

#### Scenario: 部分发货
- **WHEN** 销售订单有 100 件产品，用户先发 60 件
- **THEN** 系统创建发货单，发货数量为 60，订单仍可继续发货

#### Scenario: 发货数量超出订单数量
- **WHEN** 订单数量 100 件，已发货 80 件，用户尝试发货 30 件
- **THEN** 系统拒绝，提示"发货数量超出订单数量"

### Requirement: 发货单状态流转
系统 SHALL 支持发货单状态流转：草稿(0) → 待出库(1) → 已出库(2) → 已签收(3)。

#### Scenario: 提交出库
- **WHEN** 用户将草稿状态的发货单提交出库
- **THEN** 系统将状态改为"待出库"

#### Scenario: 确认出库
- **WHEN** 用户确认待出库状态的发货单出库
- **THEN** 系统将状态改为"已出库"，自动调用库存服务扣减库存

#### Scenario: 确认签收
- **WHEN** 用户确认已出库状态的发货单签收
- **THEN** 系统将状态改为"已签收"

#### Scenario: 非法状态转换
- **WHEN** 用户尝试非法状态转换（如从草稿直接到已出库）
- **THEN** 系统返回错误"发货单状态不允许该操作"

### Requirement: 发货单出库自动扣减库存
发货确认出库时，系统 SHALL 自动调用库存服务扣减对应产品的库存。

#### Scenario: 出库扣减库存
- **WHEN** 发货单确认出库
- **THEN** 系统调用库存服务，按发货明细扣减指定仓库的库存

#### Scenario: 库存不足拒绝出库
- **WHEN** 发货单确认出库，但某产品库存不足
- **THEN** 系统拒绝出库，提示库存不足

### Requirement: 发货单查询
系统 SHALL 支持发货单分页查询，可按发货单号、订单号、状态筛选。

#### Scenario: 按订单查询发货记录
- **WHEN** 用户查看某销售订单的发货记录
- **THEN** 系统返回该订单的所有发货单列表

### Requirement: 发货单录入物流信息
系统 SHALL 支持录入物流公司和运单号。

#### Scenario: 填写物流信息
- **WHEN** 用户创建或编辑发货单时填写物流公司和运单号
- **THEN** 系统保存物流信息

### Requirement: 出库确认
系统 SHALL 在发货出库确认时自动扣减库存并创建应收单。

#### Scenario: 出库确认扣减库存
- **WHEN** 用户确认发货出库
- **THEN** 系统扣减对应仓库的产品库存

#### Scenario: 出库确认生成应收
- **WHEN** 发货出库确认成功
- **THEN** 系统调用财务模块自动创建应收单，金额为发货金额
- **AND** 财务模块不可用时不影响出库主流程

