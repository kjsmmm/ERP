## 1. erp-quality 模块搭建

- [x] 1.1 创建 erp-quality 模块目录结构 + pom.xml
- [x] 1.2 erp-boot pom.xml 添加 erp-quality 依赖
- [x] 1.3 application.yml type-aliases-package 添加 com.erp.quality.entity
- [x] 1.4 ErrorCode 添加质量模块错误码（6030-6039）

## 2. 检验标准管理

- [x] 2.1 创建 QualityStandard / QualityStandardItem 实体 + DTO + Mapper
- [x] 2.2 实现 QualityStandardService（CRUD + 编码唯一校验）
- [x] 2.3 创建 QualityStandardController
- [x] 2.4 创建检验标准前端页面

## 3. 来料检验 (IQC)

- [x] 3.1 创建 IqInspection / IqInspectionItem 实体 + DTO + Mapper
- [x] 3.2 实现 IqInspectionService（创建检验单 + 填写结果 + 判定）
- [x] 3.3 创建 IqInspectionController
- [x] 3.4 创建来料检验前端页面

## 4. 成品检验 (OQC)

- [x] 4.1 创建 OqInspection / OqInspectionItem 实体 + DTO + Mapper
- [x] 4.2 实现 OqInspectionService（创建检验单 + 填写结果 + 判定）
- [x] 4.3 创建 OqInspectionController
- [x] 4.4 创建成品检验前端页面

## 5. 不合格品处理

- [x] 5.1 创建 DefectRecord 实体 + DTO + Mapper
- [x] 5.2 创建 defect-handling-approval.bpmn20.xml 审批流程
- [x] 5.3 实现 DefectRecordService（创建记录 + 提交审批 + 审批回调 + 执行处理）
- [x] 5.4 创建 DefectRecordController
- [x] 5.5 创建不合格品处理前端页面

## 6. 与其他模块集成

- [x] 6.1 修改 PurchaseReceiptServiceImpl，入库前检查 IQC 状态
- [x] 6.2 修改 WorkOrderServiceImpl，工单完成时自动创建 OQC 检验单

## 7. 数据库与路由

- [x] 7.1 创建质量模块数据库建表 SQL
- [x] 7.2 前端路由配置（质量管理菜单）
