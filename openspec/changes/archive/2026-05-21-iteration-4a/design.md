# 迭代 4a 技术设计

## D1: 新增 erp-production 模块

新建 `erp-production` 模块，遵循现有分层架构：controller / dto / entity / mapper / service / vo。

依赖：erp-common, erp-system, erp-product

```
erp-production/
├── controller/
│   ├── WorkshopController.java
│   ├── TeamController.java
│   ├── EquipmentTypeController.java
│   ├── EquipmentController.java
│   └── ProcessRouteController.java
├── dto/
│   ├── WorkshopDTO.java
│   ├── TeamDTO.java
│   ├── EquipmentTypeDTO.java
│   ├── EquipmentDTO.java
│   ├── ProcessRouteDTO.java
│   └── ProcessStepDTO.java
├── entity/
│   ├── Workshop.java
│   ├── Team.java
│   ├── EquipmentType.java
│   ├── Equipment.java
│   ├── ProcessRoute.java
│   └── ProcessStep.java
├── mapper/
│   ├── WorkshopMapper.java
│   ├── TeamMapper.java
│   ├── EquipmentTypeMapper.java
│   ├── EquipmentMapper.java
│   ├── ProcessRouteMapper.java
│   └── ProcessStepMapper.java
├── service/
│   ├── WorkshopService.java
│   ├── TeamService.java
│   ├── EquipmentTypeService.java
│   ├── EquipmentService.java
│   ├── ProcessRouteService.java
│   └── impl/
└── vo/
```

## D2: 工艺路线与产品的关系

工艺路线挂在产品上，一个产品可以有多条路线，但只有一条默认路线。

```
Product (erp-product)
  │
  │ 1:N (通过 productId 关联，不修改 Product 实体)
  ▼
ProcessRoute (erp-production)
  │
  │ 1:N
  ▼
ProcessStep (erp-production)
```

产品详情接口需要扩展：在 ProductDetailVO 中增加 `processRoute` 和 `processSteps` 字段。
修改 erp-product 的 ProductServiceImpl，在获取产品详情时查询默认工艺路线。

## D3: 设备类型与工序的关联

工序步骤中的 equipmentType 是文本字段（如"切割机"），设备类型表是独立管理的。
两者通过名称匹配，不做外键约束，保持灵活性。

```
ProcessStep.equipmentType = "切割机"  (文本)
EquipmentType.typeName = "切割机"    (独立表)
```

前端工序编辑时，设备类型字段使用下拉选择（从 EquipmentType 列表选），但也可以手动输入。

## D4: 数据库表设计

```sql
-- 车间
CREATE TABLE workshop (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  workshop_code VARCHAR(50) NOT NULL UNIQUE,
  workshop_name VARCHAR(100) NOT NULL,
  address VARCHAR(200),
  manager VARCHAR(50),
  phone VARCHAR(20),
  description VARCHAR(500),
  status TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 班组
CREATE TABLE team (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  team_code VARCHAR(50) NOT NULL UNIQUE,
  team_name VARCHAR(100) NOT NULL,
  workshop_id BIGINT NOT NULL,
  leader_id BIGINT,
  member_count INT DEFAULT 0,
  status TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 设备类型
CREATE TABLE equipment_type (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  type_code VARCHAR(50) NOT NULL UNIQUE,
  type_name VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 设备
CREATE TABLE equipment (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  equipment_code VARCHAR(50) NOT NULL UNIQUE,
  equipment_name VARCHAR(100) NOT NULL,
  equipment_type_id BIGINT NOT NULL,
  workshop_id BIGINT NOT NULL,
  status TINYINT DEFAULT 1,
  purchase_date DATE,
  last_maintenance_date DATE,
  next_maintenance_date DATE,
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 工艺路线
CREATE TABLE process_route (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  route_code VARCHAR(50) NOT NULL,
  route_name VARCHAR(100) NOT NULL,
  version INT DEFAULT 1,
  is_default TINYINT DEFAULT 0,
  status TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_product_id (product_id)
);

-- 工序步骤
CREATE TABLE process_step (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  route_id BIGINT NOT NULL,
  step_no INT NOT NULL,
  step_name VARCHAR(100) NOT NULL,
  standard_time DECIMAL(10,2) COMMENT '标准工时(分钟)',
  equipment_type VARCHAR(100) COMMENT '所需设备类型',
  description VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_route_id (route_id)
);
```

## D5: ErrorCode 扩展

在 erp-common ErrorCode 中新增生产模块错误码（6001-6099）。
