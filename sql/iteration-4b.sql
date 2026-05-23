-- 迭代 4b: 生产计划与工单

-- 生产计划
CREATE TABLE production_plan (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  plan_code VARCHAR(50) NOT NULL UNIQUE,
  order_id BIGINT COMMENT '关联销售订单',
  product_id BIGINT NOT NULL,
  planned_qty DECIMAL(12,2) NOT NULL,
  start_date DATE,
  end_date DATE,
  status TINYINT DEFAULT 0 COMMENT '0草稿 1已下达 2执行中 3已完成',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_product_id (product_id),
  INDEX idx_order_id (order_id)
);

-- 工单
CREATE TABLE work_order (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no VARCHAR(50) NOT NULL UNIQUE,
  plan_id BIGINT COMMENT '关联生产计划',
  product_id BIGINT NOT NULL,
  workshop_id BIGINT NOT NULL,
  route_id BIGINT COMMENT '原始工艺路线ID',
  route_name VARCHAR(100) COMMENT '快照路线名称',
  planned_qty DECIMAL(12,2) NOT NULL,
  actual_qty DECIMAL(12,2),
  status TINYINT DEFAULT 0 COMMENT '0已创建 1已下达 2生产中 3已完工 4已关闭',
  start_date DATE,
  end_date DATE,
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_plan_id (plan_id),
  INDEX idx_workshop_id (workshop_id),
  INDEX idx_status (status)
);

-- 工单步骤快照
CREATE TABLE work_order_step (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_order_id BIGINT NOT NULL,
  step_no INT NOT NULL,
  step_name VARCHAR(100) NOT NULL,
  standard_time DECIMAL(10,2) COMMENT '标准工时(分钟)',
  equipment_type VARCHAR(100) COMMENT '所需设备类型',
  description VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_work_order_id (work_order_id)
);
