-- 迭代 3a：订单管理 + 库存管理 建表脚本

-- 销售订单表
CREATE TABLE IF NOT EXISTS `sales_order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `customer_id` BIGINT NOT NULL COMMENT '客户ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1草稿 2已确认 3生产中 4已完成 5已关闭 6已取消 7已暂停)',
    `total_amount` DECIMAL(14,2) DEFAULT 0 COMMENT '总金额',
    `delivery_date` DATE COMMENT '交货日期',
    `delivery_address` VARCHAR(500) COMMENT '收货地址',
    `contact_phone` VARCHAR(32) COMMENT '联系电话',
    `created_by` BIGINT COMMENT '创建人',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_by` BIGINT COMMENT '更新人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    `factory_id` BIGINT DEFAULT 1,
    `remark` VARCHAR(500),
    UNIQUE KEY `uk_order_no` (`order_no`),
    INDEX `idx_customer_id` (`customer_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `quantity` DECIMAL(14,4) NOT NULL COMMENT '数量',
    `unit_price` DECIMAL(14,2) NOT NULL COMMENT '单价',
    `subtotal` DECIMAL(14,2) NOT NULL COMMENT '小计',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_by` BIGINT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_by` BIGINT,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    `factory_id` BIGINT DEFAULT 1,
    `remark` VARCHAR(500),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细';

-- 仓库表
CREATE TABLE IF NOT EXISTS `warehouse` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `warehouse_code` VARCHAR(32) NOT NULL COMMENT '仓库编码',
    `warehouse_name` VARCHAR(100) NOT NULL COMMENT '仓库名称',
    `address` VARCHAR(500) COMMENT '地址',
    `manager` VARCHAR(50) COMMENT '负责人',
    `phone` VARCHAR(32) COMMENT '联系电话',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0停用 1正常)',
    `created_by` BIGINT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_by` BIGINT,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    `factory_id` BIGINT DEFAULT 1,
    `remark` VARCHAR(500),
    UNIQUE KEY `uk_warehouse_code` (`warehouse_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库';

-- 库存表
CREATE TABLE IF NOT EXISTS `inventory` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
    `on_hand_qty` DECIMAL(14,4) NOT NULL DEFAULT 0 COMMENT '实物库存量',
    `reserved_qty` DECIMAL(14,4) NOT NULL DEFAULT 0 COMMENT '预留量',
    `created_by` BIGINT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_by` BIGINT,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    `factory_id` BIGINT DEFAULT 1,
    `remark` VARCHAR(500),
    UNIQUE KEY `uk_product_warehouse` (`product_id`, `warehouse_id`),
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存';

-- 库存流水表
CREATE TABLE IF NOT EXISTS `stock_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
    `record_type` VARCHAR(20) NOT NULL COMMENT '类型(INBOUND/OUTBOUND/RESERVE/RELEASE)',
    `quantity` DECIMAL(14,4) NOT NULL COMMENT '数量',
    `reference_no` VARCHAR(64) COMMENT '关联单据号',
    `reference_type` VARCHAR(20) COMMENT '关联单据类型',
    `reference_id` BIGINT COMMENT '关联单据ID',
    `created_by` BIGINT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_by` BIGINT,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    `factory_id` BIGINT DEFAULT 1,
    `remark` VARCHAR(500),
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_warehouse_id` (`warehouse_id`),
    INDEX `idx_record_type` (`record_type`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水';
