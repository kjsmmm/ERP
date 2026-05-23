-- ============================================================
-- Iteration 6b: 财务管理 - 应收账款 & 应付账款
-- ============================================================

-- 应收账款
CREATE TABLE ar_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  receivable_no VARCHAR(50) NOT NULL UNIQUE COMMENT '应收单号',
  delivery_id BIGINT NOT NULL COMMENT '关联发货单ID',
  order_id BIGINT COMMENT '关联销售订单ID',
  customer_id BIGINT COMMENT '客户ID',
  amount DECIMAL(12,2) NOT NULL COMMENT '应收金额',
  paid_amount DECIMAL(12,2) DEFAULT 0 COMMENT '已收金额',
  invoice_no VARCHAR(100) COMMENT '发票号',
  invoice_date DATE COMMENT '开票日期',
  invoice_status TINYINT DEFAULT 0 COMMENT '0=未开票 1=已开票',
  status TINYINT DEFAULT 0 COMMENT '0=未收 1=部分收 2=已收',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_delivery_id (delivery_id),
  INDEX idx_customer_id (customer_id),
  INDEX idx_receivable_no (receivable_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应收账款';

-- 收款记录
CREATE TABLE ar_payment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  payment_no VARCHAR(50) NOT NULL UNIQUE COMMENT '收款单号',
  ar_id BIGINT NOT NULL COMMENT '应收单ID',
  amount DECIMAL(12,2) NOT NULL COMMENT '收款金额',
  payment_method VARCHAR(50) COMMENT '收款方式',
  payment_date DATE COMMENT '收款日期',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_ar_id (ar_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收款记录';

-- 应付账款
CREATE TABLE ap_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  payable_no VARCHAR(50) NOT NULL UNIQUE COMMENT '应付单号',
  receipt_id BIGINT NOT NULL COMMENT '关联入库单ID',
  purchase_order_id BIGINT COMMENT '关联采购单ID',
  supplier_id BIGINT COMMENT '供应商ID',
  amount DECIMAL(12,2) NOT NULL COMMENT '应付金额',
  paid_amount DECIMAL(12,2) DEFAULT 0 COMMENT '已付金额',
  invoice_no VARCHAR(100) COMMENT '发票号',
  invoice_date DATE COMMENT '来票日期',
  invoice_status TINYINT DEFAULT 0 COMMENT '0=未开票 1=已开票',
  status TINYINT DEFAULT 0 COMMENT '0=未付 1=部分付 2=已付',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_receipt_id (receipt_id),
  INDEX idx_supplier_id (supplier_id),
  INDEX idx_payable_no (payable_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应付账款';

-- 付款记录
CREATE TABLE ap_payment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  payment_no VARCHAR(50) NOT NULL UNIQUE COMMENT '付款单号',
  ap_id BIGINT NOT NULL COMMENT '应付单ID',
  amount DECIMAL(12,2) NOT NULL COMMENT '付款金额',
  payment_method VARCHAR(50) COMMENT '付款方式',
  payment_date DATE COMMENT '付款日期',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_ap_id (ap_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='付款记录';
