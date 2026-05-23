## Why

销售发货和采购入库已完成，但缺少对资金流动的管理。企业需要知道：客户欠多少钱、该付供应商多少钱、发票开了没有、款收了没有。补齐应收应付管理，打通业务-财务链路。

## What Changes

- 新建 erp-finance 模块，管理应收应付
- 销售发货确认时自动生成应收单（跨模块调用）
- 采购入库确认时自动生成应付单（跨模块调用）
- 应收/应付单记录发票信息（发票号、开票日期、开票状态）
- 支持收款/付款登记，允许多次部分收款/付款核销
- 支持应收/应付状态跟踪：未收/付 → 部分收/付 → 已收/付

## Capabilities

### New Capabilities

- `accounts-receivable`: 应收账款管理，销售发货自动生成应收单，支持发票登记、多次收款核销、状态跟踪
- `accounts-payable`: 应付账款管理，采购入库自动生成应付单，支持发票登记、多次付款核销、状态跟踪

### Modified Capabilities

- `sales-delivery`: 发货出库确认后自动创建应收单
- `purchase-receipt`: 采购入库确认后自动创建应付单

## Impact

- **后端模块**: 新建 erp-finance 模块（应收/应付实体、服务、控制器）
- **跨模块依赖**: 销售发货、采购入库完成后通过 ApplicationContext 触发应收/应付单创建
- **前端模块**: 新增应收管理、应付管理页面
- **数据库**: 新增 ar_record、ar_payment、ap_record、ap_payment 表
