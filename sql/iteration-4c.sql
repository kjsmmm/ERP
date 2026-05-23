-- 迭代 4c: 报工与物料结算

-- 报工记录
CREATE TABLE work_report (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_order_id BIGINT NOT NULL,
  step_no INT NOT NULL,
  step_name VARCHAR(100),
  report_qty DECIMAL(12,2) NOT NULL COMMENT '报工数量',
  actual_hours DECIMAL(10,2) COMMENT '实际工时(分钟)',
  report_time DATETIME NOT NULL,
  reporter_id BIGINT COMMENT '报工人ID',
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_work_order_id (work_order_id)
);
