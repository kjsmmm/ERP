-- 迭代 5a: 采购管理
-- 供应商、采购申请、采购申请明细、采购单、采购单明细、采购入库、采购入库明细

-- 供应商
CREATE TABLE supplier (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  supplier_code VARCHAR(50) NOT NULL UNIQUE,
  supplier_name VARCHAR(100) NOT NULL,
  contact_name VARCHAR(50),
  phone VARCHAR(20),
  address VARCHAR(200),
  status TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 采购申请
CREATE TABLE purchase_request (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_no VARCHAR(50) NOT NULL UNIQUE,
  request_type TINYINT NOT NULL COMMENT '1=原材料 2=耗材 3=其他',
  status TINYINT DEFAULT 0 COMMENT '0=草稿 1=审批中 2=已通过 3=已驳回',
  process_instance_id VARCHAR(100),
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_request_no (request_no),
  INDEX idx_status (status)
);

-- 采购申请明细
CREATE TABLE purchase_request_item (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  purchase_request_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  unit VARCHAR(20),
  remark VARCHAR(200),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_request_id (purchase_request_id)
);

-- 采购单
CREATE TABLE purchase_order (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no VARCHAR(50) NOT NULL UNIQUE,
  supplier_id BIGINT NOT NULL,
  purchase_request_id BIGINT,
  status TINYINT DEFAULT 0 COMMENT '0=草稿 1=已确认 2=部分入库 3=已完成 4=已取消',
  total_amount DECIMAL(12,2) DEFAULT 0,
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_order_no (order_no),
  INDEX idx_supplier_id (supplier_id),
  INDEX idx_status (status)
);

-- 采购单明细
CREATE TABLE purchase_order_item (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  purchase_order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(10,2),
  amount DECIMAL(12,2),
  unit VARCHAR(20),
  remark VARCHAR(200),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_order_id (purchase_order_id)
);

-- 采购入库
CREATE TABLE purchase_receipt (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  receipt_no VARCHAR(50) NOT NULL UNIQUE,
  purchase_order_id BIGINT NOT NULL,
  warehouse_id BIGINT NOT NULL,
  status TINYINT DEFAULT 1 COMMENT '0=待入库 1=已入库',
  inspection_status TINYINT DEFAULT 0 COMMENT '0=待检验 1=合格 2=不合格',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_receipt_no (receipt_no),
  INDEX idx_order_id (purchase_order_id)
);

-- 采购入库明细
CREATE TABLE purchase_receipt_item (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  purchase_receipt_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  unit VARCHAR(20),
  remark VARCHAR(200),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_receipt_id (purchase_receipt_id)
);
