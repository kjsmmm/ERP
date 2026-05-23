-- 迭代3b 数据库变更

-- 销售订单表增加字段（审批流程支持）
ALTER TABLE sales_order ADD COLUMN process_instance_id VARCHAR(64) COMMENT '流程实例ID';
ALTER TABLE sales_order ADD COLUMN pending_data TEXT COMMENT '待审批变更数据（JSON）';

-- 客户产品专属价格表
CREATE TABLE IF NOT EXISTS customer_product_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    price DECIMAL(12,4) NOT NULL COMMENT '客户专属价格',
    remark VARCHAR(255) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_customer_product (customer_id, product_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户产品专属价格表';

-- 库存表增加安全库存字段
ALTER TABLE inventory ADD COLUMN safety_stock DECIMAL(12,4) DEFAULT 0 COMMENT '安全库存量';
