-- ============================================================
-- Iteration 5b: 质量管理模块
-- 检验标准、来料检验(IQC)、成品检验(OQC)、不合格品处理
-- ============================================================

-- 检验标准
CREATE TABLE quality_standard (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  standard_code VARCHAR(50) NOT NULL UNIQUE COMMENT '标准编码',
  standard_name VARCHAR(100) NOT NULL COMMENT '标准名称',
  applicable_type TINYINT COMMENT '1=原材料 2=半成品 3=成品',
  category_id BIGINT COMMENT '关联产品分类，为空表示通用',
  status TINYINT DEFAULT 1 COMMENT '0=禁用 1=启用',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_standard_code (standard_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验标准';

-- 检验项目
CREATE TABLE quality_standard_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  standard_id BIGINT NOT NULL COMMENT '检验标准ID',
  item_name VARCHAR(100) NOT NULL COMMENT '检验项目名称',
  inspection_method VARCHAR(200) COMMENT '检验方法',
  standard_value VARCHAR(200) COMMENT '标准值',
  judgment_rule VARCHAR(200) COMMENT '判定规则',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_standard_id (standard_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验标准项目';

-- 来料检验
CREATE TABLE iq_inspection (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  inspection_no VARCHAR(50) NOT NULL UNIQUE COMMENT '检验单号',
  purchase_order_id BIGINT NOT NULL COMMENT '采购单ID',
  supplier_id BIGINT COMMENT '供应商ID',
  inspection_result TINYINT DEFAULT 0 COMMENT '0=待检验 1=合格 2=不合格',
  status TINYINT DEFAULT 0 COMMENT '0=待检验 1=检验中 2=已完成',
  inspector_id BIGINT COMMENT '检验员ID',
  inspection_time DATETIME COMMENT '检验时间',
  remark VARCHAR(500) COMMENT '备注',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_purchase_order_id (purchase_order_id),
  INDEX idx_inspection_no (inspection_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='来料检验';

-- 来料检验项目
CREATE TABLE iq_inspection_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  iq_inspection_id BIGINT NOT NULL COMMENT '来料检验单ID',
  item_name VARCHAR(100) COMMENT '检验项目名称',
  inspection_method VARCHAR(200) COMMENT '检验方法',
  standard_value VARCHAR(200) COMMENT '标准值',
  actual_value VARCHAR(200) COMMENT '实际值',
  judgment TINYINT DEFAULT 0 COMMENT '0=未判定 1=合格 2=不合格',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_inspection_id (iq_inspection_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='来料检验项目';

-- 成品检验
CREATE TABLE oq_inspection (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  inspection_no VARCHAR(50) NOT NULL UNIQUE COMMENT '检验单号',
  work_order_id BIGINT NOT NULL COMMENT '工单ID',
  product_id BIGINT COMMENT '产品ID',
  quantity INT COMMENT '数量',
  inspection_result TINYINT DEFAULT 0 COMMENT '0=待检验 1=合格 2=不合格',
  status TINYINT DEFAULT 0 COMMENT '0=待检验 1=检验中 2=已完成',
  inspector_id BIGINT COMMENT '检验员ID',
  inspection_time DATETIME COMMENT '检验时间',
  remark VARCHAR(500) COMMENT '备注',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_work_order_id (work_order_id),
  INDEX idx_inspection_no (inspection_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成品检验';

-- 成品检验项目
CREATE TABLE oq_inspection_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  oq_inspection_id BIGINT NOT NULL COMMENT '成品检验单ID',
  item_name VARCHAR(100) COMMENT '检验项目名称',
  inspection_method VARCHAR(200) COMMENT '检验方法',
  standard_value VARCHAR(200) COMMENT '标准值',
  actual_value VARCHAR(200) COMMENT '实际值',
  judgment TINYINT DEFAULT 0 COMMENT '0=未判定 1=合格 2=不合格',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_inspection_id (oq_inspection_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成品检验项目';

-- 不合格品处理
CREATE TABLE defect_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  record_no VARCHAR(50) NOT NULL UNIQUE COMMENT '记录编号',
  source_type TINYINT COMMENT '1=IQC 2=OQC',
  source_id BIGINT COMMENT '检验单ID',
  product_id BIGINT COMMENT '产品ID',
  quantity INT COMMENT '不合格数量',
  defect_reason VARCHAR(500) COMMENT '不合格原因',
  handle_type TINYINT COMMENT '1=退货 2=返工 3=报废 4=让步接收',
  handle_remark VARCHAR(500) COMMENT '处理备注',
  status TINYINT DEFAULT 0 COMMENT '0=待审批 1=审批中 2=已通过 3=已驳回 4=已处理',
  process_instance_id VARCHAR(100) COMMENT 'Flowable流程实例ID',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_record_no (record_no),
  INDEX idx_source (source_type, source_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='不合格品处理';
