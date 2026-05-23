## 1. erp-production 模块搭建

- [x] 1.1 创建 erp-production 模块目录结构 + pom.xml
- [x] 1.2 erp-boot pom.xml 添加 erp-production 依赖
- [x] 1.3 application.yml type-aliases-package 添加 com.erp.production.entity
- [x] 1.4 ErrorCode 添加生产模块错误码（6001-6099）

## 2. 车间管理

- [x] 2.1 创建 Workshop 实体
- [x] 2.2 创建 WorkshopDTO / WorkshopMapper
- [x] 2.3 实现 WorkshopService（CRUD + 编码唯一校验 + 删除时检查班组）
- [x] 2.4 创建 WorkshopController
- [x] 2.5 创建车间管理前端页面

## 3. 班组管理

- [x] 3.1 创建 Team 实体
- [x] 3.2 创建 TeamDTO / TeamMapper
- [x] 3.3 实现 TeamService（CRUD + 关联车间 + 填充负责人姓名）
- [x] 3.4 创建 TeamController
- [x] 3.5 创建班组管理前端页面

## 4. 设备管理

- [x] 4.1 创建 EquipmentType 实体
- [x] 4.2 创建 EquipmentTypeDTO / EquipmentTypeMapper
- [x] 4.3 实现 EquipmentTypeService（CRUD + 删除时检查引用）
- [x] 4.4 创建 EquipmentTypeController
- [x] 4.5 创建 Equipment 实体
- [x] 4.6 创建 EquipmentDTO / EquipmentMapper
- [x] 4.7 实现 EquipmentService（CRUD + 关联车间和设备类型）
- [x] 4.8 创建 EquipmentController
- [x] 4.9 创建设备类型管理前端页面
- [x] 4.10 创建设备管理前端页面

## 5. 工艺路线

- [x] 5.1 创建 ProcessRoute 实体
- [x] 5.2 创建 ProcessStep 实体
- [x] 5.3 创建 ProcessRouteDTO / ProcessStepDTO / ProcessRouteMapper / ProcessStepMapper
- [x] 5.4 实现 ProcessRouteService（路线CRUD + 默认路线管理 + 步骤CRUD）
- [x] 5.5 创建 ProcessRouteController（路线+步骤的增删改查）
- [x] 5.6 修改 ProductDetailVO 增加工艺路线字段
- [x] 5.7 修改 ProductServiceImpl 获取详情时加载默认工艺路线
- [x] 5.8 创建工艺路线前端页面（在产品详情中以Tab形式展示）

## 6. 数据库与路由

- [x] 6.1 创建生产模块数据库建表 SQL
- [x] 6.2 前端路由配置（生产管理菜单）
