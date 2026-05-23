## 1. Flowable 基础集成

- [x] 1.1 erp-boot pom.xml 添加 flowable-spring-boot-starter 依赖
- [x] 1.2 application.yml 添加 Flowable 配置（数据库、async、历史级别）
- [x] 1.3 创建 WorkflowService 封装发起/查询/审批/驳回操作
- [x] 1.4 创建 WorkflowConfig 配置类
- [x] 1.5 WorkflowController 提供待办/已办/审批接口
- [x] 1.6 创建第一个 BPMN 流程定义（订单变更审批：申请人→主管→完成）

## 2. 订单变更审批

- [x] 2.1 OrderService 增加变更审批联动（编辑已确认订单时触发流程）
- [x] 2.2 OrderService 增加审批回调（通过→应用变更，驳回→丢弃变更）
- [x] 2.3 订单详情页展示审批状态和审批记录

## 3. 客户级别定价

- [x] 3.1 创建 CustomerProductPrice 实体
- [x] 3.2 创建 CustomerPriceMapper
- [x] 3.3 实现 CustomerPriceService：客户专属价格 CRUD
- [x] 3.4 OrderService 创建订单时自动带出客户价格（客户价格 > 标准售价）
- [x] 3.5 创建 CustomerPriceController
- [x] 3.6 创建客户价格相关数据库建表 SQL

## 4. 库存预警

- [x] 4.1 Inventory 实体增加 safety_stock 字段
- [x] 4.2 InventoryService 增加预警查询（on_hand_qty < safety_stock）
- [x] 4.3 库存查询页增加预警标识

## 5. 前端页面

- [x] 5.1 我的待办页：Flowable 待审批任务列表
- [x] 5.2 审批操作：同意/驳回 + 填写审批意见
- [x] 5.3 客户价格管理页（在客户详情中 Tab 或独立页面）

## 6. 联调测试

- [x] 6.1 订单变更 → 触发审批 → 审批通过/驳回 流程验证
- [x] 6.2 客户价格优先级验证（客户专属价格 > 标准售价）
- [x] 6.3 库存预警触发验证
