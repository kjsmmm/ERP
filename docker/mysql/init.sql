-- ERP 系统数据库初始化脚本
-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS erp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE erp;

-- =============================================
-- 系统管理模块表结构
-- =============================================

-- 1. 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    dept_code VARCHAR(50) COMMENT '部门编码',
    sort_order INT DEFAULT 0 COMMENT '排序',
    leader VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1正常）',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 2. 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别（0未知 1男 2女）',
    dept_id BIGINT COMMENT '部门ID',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1正常）',
    login_ip VARCHAR(50) COMMENT '最后登录IP',
    login_date DATETIME COMMENT '最后登录时间',
    login_fail_count INT DEFAULT 0 COMMENT '登录失败次数',
    lock_time DATETIME COMMENT '锁定时间',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    UNIQUE INDEX uk_username (username),
    INDEX idx_dept_id (dept_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 3. 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    sort_order INT DEFAULT 0 COMMENT '排序',
    data_scope TINYINT DEFAULT 1 COMMENT '数据范围（1全部 2本部门及下级 3本部门 4仅本人）',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1正常）',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    UNIQUE INDEX uk_role_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 4. 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    perm_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    perm_code VARCHAR(100) COMMENT '权限编码',
    perm_type TINYINT DEFAULT 1 COMMENT '权限类型（1目录 2菜单 3按钮）',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    visible TINYINT DEFAULT 1 COMMENT '是否可见（0隐藏 1显示）',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1正常）',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_parent_id (parent_id),
    INDEX idx_perm_type (perm_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 5. 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX uk_user_role (user_id, role_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 6. 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX uk_role_permission (role_id, permission_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 7. 操作日志表
CREATE TABLE IF NOT EXISTS sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    module VARCHAR(50) COMMENT '操作模块',
    operation VARCHAR(50) COMMENT '操作类型',
    method VARCHAR(200) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_method VARCHAR(10) COMMENT 'HTTP方法',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '响应结果',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人名称',
    operator_ip VARCHAR(50) COMMENT '操作IP',
    execute_time BIGINT COMMENT '执行时间（毫秒）',
    status TINYINT DEFAULT 1 COMMENT '操作状态（0失败 1成功）',
    error_msg TEXT COMMENT '错误信息',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    INDEX idx_operator_id (operator_id),
    INDEX idx_operation (operation),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 8. 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type TINYINT DEFAULT 1 COMMENT '配置类型（1系统 2自定义）',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1正常）',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    UNIQUE INDEX uk_config_key (config_key),
    INDEX idx_config_type (config_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =============================================
-- 插入初始数据
-- =============================================

-- 1. 默认部门
INSERT INTO sys_dept (id, parent_id, dept_name, dept_code, sort_order, status, remark) VALUES
(1, 0, '总公司', 'HQ', 1, 1, '总公司'),
(2, 1, '技术部', 'TECH', 1, 1, '技术部门'),
(3, 1, '销售部', 'SALES', 2, 1, '销售部门'),
(4, 1, '生产部', 'PROD', 3, 1, '生产部门'),
(5, 1, '仓库部', 'WH', 4, 1, '仓库部门'),
(6, 1, '采购部', 'PURCHASE', 5, 1, '采购部门'),
(7, 1, '质量部', 'QC', 6, 1, '质量部门'),
(8, 1, '财务部', 'FINANCE', 7, 1, '财务部门'),
(9, 1, '人事部', 'HR', 8, 1, '人事部门');

-- 2. 默认角色
INSERT INTO sys_role (id, role_name, role_code, sort_order, data_scope, status, remark) VALUES
(1, '系统管理员', 'admin', 1, 1, 1, '拥有所有权限'),
(2, '厂长/总经理', 'director', 2, 1, 1, '全部数据查看 + 审批权限'),
(3, '销售主管', 'sales_manager', 3, 2, 1, '客户/订单管理 + 本部门数据'),
(4, '车间主管', 'workshop_manager', 4, 3, 1, '生产管理 + 本车间数据'),
(5, '仓库管理员', 'warehouse_admin', 5, 1, 1, '库存管理 + 全部库存数据'),
(6, '采购员', 'purchaser', 6, 4, 1, '采购管理 + 本人数据'),
(7, '质检员', 'qc_inspector', 7, 3, 1, '质量管理 + 检验操作'),
(8, '普通员工', 'employee', 8, 4, 1, '基础查看权限');

-- 3. 默认管理员用户（密码: admin123456，BCrypt加密）
INSERT INTO sys_user (id, username, password, nickname, real_name, dept_id, status, remark) VALUES
(1, 'admin', '$2b$12$s1xG1lCs3I5pg4cRqBXc3uKbO.fTowpT8gxn60hP8bWKQbSjcRv2q', '系统管理员', '系统管理员', 1, 1, '默认管理员账号');

-- 4. 管理员角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 5. 默认权限（菜单和按钮）
INSERT INTO sys_permission (id, parent_id, perm_name, perm_code, perm_type, path, component, icon, sort_order) VALUES
-- 系统管理目录
(1, 0, '系统管理', 'system', 1, '/system', '', 'setting', 1),
-- 用户管理
(2, 1, '用户管理', 'system:user', 2, '/system/user', 'system/user/index', 'user', 1),
(3, 2, '用户新增', 'system:user:add', 3, '', '', '', 1),
(4, 2, '用户编辑', 'system:user:edit', 3, '', '', '', 2),
(5, 2, '用户删除', 'system:user:delete', 3, '', '', '', 3),
(6, 2, '重置密码', 'system:user:resetpwd', 3, '', '', '', 4),
-- 角色管理
(7, 1, '角色管理', 'system:role', 2, '/system/role', 'system/role/index', 'peoples', 2),
(8, 7, '角色新增', 'system:role:add', 3, '', '', '', 1),
(9, 7, '角色编辑', 'system:role:edit', 3, '', '', '', 2),
(10, 7, '角色删除', 'system:role:delete', 3, '', '', '', 3),
-- 部门管理
(11, 1, '部门管理', 'system:dept', 2, '/system/dept', 'system/dept/index', 'tree', 3),
(12, 11, '部门新增', 'system:dept:add', 3, '', '', '', 1),
(13, 11, '部门编辑', 'system:dept:edit', 3, '', '', '', 2),
(14, 11, '部门删除', 'system:dept:delete', 3, '', '', '', 3),
-- 权限管理
(15, 1, '权限管理', 'system:permission', 2, '/system/permission', 'system/permission/index', 'permission', 4),
(16, 15, '权限查看', 'system:permission:view', 3, '', '', '', 1),
(17, 15, '权限新增', 'system:permission:add', 3, '', '', '', 2),
(18, 15, '权限编辑', 'system:permission:edit', 3, '', '', '', 3),
(19, 15, '权限删除', 'system:permission:delete', 3, '', '', '', 4),
-- 操作日志
(20, 1, '操作日志', 'system:log', 2, '/system/log', 'system/log/index', 'log', 5),
(21, 20, '日志查询', 'system:log:query', 3, '', '', '', 1),
(22, 20, '日志删除', 'system:log:delete', 3, '', '', '', 2);

-- 客户管理目录和权限
INSERT INTO sys_permission (id, parent_id, perm_name, perm_code, perm_type, path, component, icon, sort_order) VALUES
(30, 0, '客户管理', 'customer', 1, '/customer', '', 'customer', 2),
(31, 30, '客户列表', 'customer:view', 2, '/customer/list', 'customer/list/index', 'list', 1),
(32, 31, '客户新增', 'customer:add', 3, '', '', '', 1),
(33, 31, '客户编辑', 'customer:edit', 3, '', '', '', 2),
(34, 31, '客户删除', 'customer:delete', 3, '', '', '', 3);

-- 产品管理目录和权限
INSERT INTO sys_permission (id, parent_id, perm_name, perm_code, perm_type, path, component, icon, sort_order) VALUES
(40, 0, '产品管理', 'product', 1, '/product', '', 'goods', 3),
(41, 40, '产品列表', 'product:view', 2, '/product/list', 'product/list/index', 'list', 1),
(42, 41, '产品新增', 'product:add', 3, '', '', '', 1),
(43, 41, '产品编辑', 'product:edit', 3, '', '', '', 2),
(44, 41, '产品删除', 'product:delete', 3, '', '', '', 3),
(45, 40, '分类管理', 'product:category', 2, '/product/category', 'product/category/index', 'tree', 2),
(46, 45, '分类新增', 'product:category:add', 3, '', '', '', 1),
(47, 45, '分类编辑', 'product:category:edit', 3, '', '', '', 2),
(48, 45, '分类删除', 'product:category:delete', 3, '', '', '', 3);

-- 6. 管理员拥有所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- =============================================
-- 客户管理模块表结构
-- =============================================

-- 8. 客户表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '客户ID',
    customer_code VARCHAR(50) NOT NULL COMMENT '客户编码',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户名称',
    customer_type TINYINT DEFAULT 1 COMMENT '客户类型（1国内 2国外）',
    industry VARCHAR(50) COMMENT '行业',
    customer_level TINYINT DEFAULT 3 COMMENT '客户等级（1A 2B 3C 4D）',
    source VARCHAR(50) COMMENT '客户来源',
    tax_number VARCHAR(30) COMMENT '税号',
    bank_name VARCHAR(100) COMMENT '开户行',
    bank_account VARCHAR(50) COMMENT '银行账号',
    payment_terms VARCHAR(50) COMMENT '账期',
    credit_limit DECIMAL(12,2) DEFAULT 0 COMMENT '信用额度',
    address VARCHAR(200) COMMENT '地址',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1启用）',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    UNIQUE INDEX uk_customer_code (customer_code),
    INDEX idx_customer_name (customer_name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户表';

-- 9. 客户联系人表
CREATE TABLE IF NOT EXISTS customer_contact (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '联系人ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    contact_name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    position VARCHAR(50) COMMENT '职位',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    is_primary TINYINT DEFAULT 0 COMMENT '是否主要联系人（0否 1是）',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_customer_id (customer_id),
    INDEX idx_is_primary (is_primary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户联系人表';

-- 10. 客户跟进记录表
CREATE TABLE IF NOT EXISTS customer_follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '跟进ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    contact_id BIGINT COMMENT '联系人ID',
    follow_type TINYINT NOT NULL COMMENT '跟进类型（1电话 2拜访 3邮件 4微信）',
    content TEXT COMMENT '跟进内容',
    follow_time DATETIME NOT NULL COMMENT '跟进时间',
    next_follow_time DATETIME COMMENT '下次跟进时间',
    operator_id BIGINT COMMENT '跟进人ID',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_customer_id (customer_id),
    INDEX idx_contact_id (contact_id),
    INDEX idx_follow_time (follow_time),
    INDEX idx_operator_id (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户跟进记录表';

-- 11. 系统配置
INSERT INTO sys_config (config_name, config_key, config_value, config_type, remark) VALUES
('用户初始密码', 'sys.user.initPassword', 'admin123456', 1, '新建用户的初始密码'),
('账号锁定次数', 'sys.login.maxRetryCount', '5', 1, '登录失败锁定次数'),
('账号锁定时间', 'sys.login.lockTime', '30', 1, '登录失败锁定时间（分钟）'),
('Token有效期', 'sys.token.accessTokenExpire', '15', 1, 'Access Token有效期（分钟）'),
('RefreshToken有效期', 'sys.token.refreshTokenExpire', '10080', 1, 'Refresh Token有效期（分钟）');

-- =============================================
-- 产品管理模块表结构
-- =============================================

-- 12. 产品分类表
CREATE TABLE IF NOT EXISTS product_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID（0=顶级）',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1正常）',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品分类表';

-- 13. 产品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '产品ID',
    product_code VARCHAR(50) NOT NULL COMMENT '产品编码',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    category_id BIGINT COMMENT '分类ID',
    product_type TINYINT NOT NULL COMMENT '产品类型（1原材料 2半成品 3成品）',
    spec VARCHAR(200) COMMENT '规格型号',
    unit VARCHAR(20) COMMENT '单位',
    weight DECIMAL(10,3) COMMENT '重量(kg)',
    standard_cost DECIMAL(12,2) COMMENT '标准成本',
    standard_price DECIMAL(12,2) COMMENT '标准售价',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1正常）',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    UNIQUE INDEX uk_product_code (product_code),
    INDEX idx_product_name (product_name),
    INDEX idx_category_id (category_id),
    INDEX idx_product_type (product_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表';

-- 14. 产品图片表
CREATE TABLE IF NOT EXISTS product_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    image_url VARCHAR(500) NOT NULL COMMENT '图片路径',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_primary TINYINT DEFAULT 0 COMMENT '是否主图（0否 1是）',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_product_id (product_id),
    INDEX idx_is_primary (is_primary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品图片表';

-- 15. BOM物料清单表
CREATE TABLE IF NOT EXISTS bom_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'BOM项ID',
    product_id BIGINT NOT NULL COMMENT '父产品ID',
    material_id BIGINT NOT NULL COMMENT '子物料ID（指向product表）',
    quantity DECIMAL(12,4) NOT NULL COMMENT '用量',
    waste_rate DECIMAL(5,2) DEFAULT 0 COMMENT '损耗率(%)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0正常 1删除）',
    factory_id BIGINT DEFAULT 1 COMMENT '工厂ID',
    remark VARCHAR(500) COMMENT '备注',
    UNIQUE INDEX uk_product_material (product_id, material_id),
    INDEX idx_product_id (product_id),
    INDEX idx_material_id (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BOM物料清单表';

-- =============================================
-- 产品管理模块初始数据
-- =============================================

-- 默认产品分类
INSERT INTO product_category (id, name, parent_id, sort_order, status) VALUES
(1, '原材料', 0, 1, 1),
(2, '半成品', 0, 2, 1),
(3, '成品', 0, 3, 1),
(4, '五金件', 1, 1, 1),
(5, '电子元件', 1, 2, 1),
(6, '包装材料', 1, 3, 1),
(7, '塑料件', 1, 4, 1);
