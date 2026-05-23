-- ============================================================
-- Iteration 6a: 销售发货与退货
-- ============================================================

-- 销售发货单
CREATE TABLE sales_delivery (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  delivery_no VARCHAR(50) NOT NULL UNIQUE COMMENT '发货单号',
  order_id BIGINT NOT NULL COMMENT '销售订单ID',
  customer_id BIGINT COMMENT '客户ID',
  delivery_date DATE COMMENT '发货日期',
  logistics_company VARCHAR(100) COMMENT '物流公司',
  tracking_no VARCHAR(100) COMMENT '运单号',
  warehouse_id BIGINT COMMENT '出库仓库ID',
  status TINYINT DEFAULT 0 COMMENT '0=草稿 1=待出库 2=已出库 3=已签收',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_order_id (order_id),
  INDEX idx_delivery_no (delivery_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售发货单';

-- 销售发货明细
CREATE TABLE sales_delivery_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  delivery_id BIGINT NOT NULL COMMENT '发货单ID',
  product_id BIGINT NOT NULL COMMENT '产品ID',
  quantity INT NOT NULL COMMENT '发货数量',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_delivery_id (delivery_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售发货明细';

-- 销售退货单
CREATE TABLE sales_return (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  return_no VARCHAR(50) NOT NULL UNIQUE COMMENT '退货单号',
  delivery_id BIGINT NOT NULL COMMENT '原发货单ID',
  order_id BIGINT COMMENT '销售订单ID',
  customer_id BIGINT COMMENT '客户ID',
  return_reason VARCHAR(500) COMMENT '退货原因',
  warehouse_id BIGINT COMMENT '退货入库仓库ID',
  status TINYINT DEFAULT 0 COMMENT '0=待审批 1=审批中 2=已通过 3=已驳回 4=已入库',
  process_instance_id VARCHAR(100) COMMENT 'Flowable流程实例ID',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_delivery_id (delivery_id),
  INDEX idx_return_no (return_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售退货单';

-- 销售退货明细
CREATE TABLE sales_return_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  return_id BIGINT NOT NULL COMMENT '退货单ID',
  product_id BIGINT NOT NULL COMMENT '产品ID',
  quantity INT NOT NULL COMMENT '退货数量',
  reason VARCHAR(500) COMMENT '退货原因',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_return_id (return_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售退货明细';
