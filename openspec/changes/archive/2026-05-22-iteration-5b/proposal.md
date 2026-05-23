## Why

采购管理和生产管理已完成，但缺少质量检验环节。来料检验(IQC)和成品检验(OQC)是工厂保证产品质量的关键流程，不合格品需要规范化的处理流程和审批。

## What Changes

- 新增检验标准管理：定义产品分类的检验项目和标准值
- 新增来料检验(IQC)：采购入库前的质量检验，关联采购单
- 新增成品检验(OQC)：工单完成后的成品检验，关联工单
- 新增不合格品处理：检验不合格时的处理流程（退货/返工/报废/让步接收），带 Flowable 审批
- 新增质量统计报表：合格率统计、不合格原因分析

## Capabilities

### New Capabilities

- `inspection-standard`: 检验标准管理，定义检验项目和标准值，按产品分类复用
- `iqc-inspection`: 来料检验(IQC)，采购到货后检验，合格才能入库
- `oqc-inspection`: 成品检验(OQC)，工单完成时检验，合格才能入成品库
- `defect-handling`: 不合格品处理，支持退货/返工/报废/让步接收，带 Flowable 审批

### Modified Capabilities

- `purchase-receipt`: 采购入库需等待 IQC 检验通过后才能执行
- `work-order`: 工单完成时触发 OQC 检验

## Impact

**后端模块：**
- 新增 erp-quality 模块（或在 erp-purchase/erp-production 中扩展）
- 需要 erp-common 中的 WorkflowService 支持新的审批流程

**前端页面：**
- 新增质量管理菜单（检验标准、IQC、OQC、不合格品处理）
- 新增质量统计报表页面

**数据库：**
- 新增 6 张表：quality_standard, quality_standard_item, iq_inspection, iq_inspection_item, oq_inspection, defect_record
