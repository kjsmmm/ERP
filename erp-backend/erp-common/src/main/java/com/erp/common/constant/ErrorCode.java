package com.erp.common.constant;

/**
 * 错误码常量
 */
public interface ErrorCode {

    // ==================== 通用错误码 ====================

    /** 成功 */
    int SUCCESS = 200;

    /** 参数错误 */
    int BAD_REQUEST = 400;

    /** 未授权 */
    int UNAUTHORIZED = 401;

    /** 禁止访问 */
    int FORBIDDEN = 403;

    /** 资源不存在 */
    int NOT_FOUND = 404;

    /** 请求方法不支持 */
    int METHOD_NOT_ALLOWED = 405;

    /** 系统内部错误 */
    int INTERNAL_SERVER_ERROR = 500;

    // ==================== 业务错误码 ====================

    /** 用户名或密码错误 */
    int LOGIN_FAIL = 1001;

    /** 账号已被锁定 */
    int ACCOUNT_LOCKED = 1002;

    /** 账号已被禁用 */
    int ACCOUNT_DISABLED = 1003;

    /** Token 已过期 */
    int TOKEN_EXPIRED = 1004;

    /** Token 无效 */
    int TOKEN_INVALID = 1005;

    /** 验证码错误 */
    int CAPTCHA_ERROR = 1006;

    /** 用户名已存在 */
    int USERNAME_EXISTS = 1007;

    /** 角色编码已存在 */
    int ROLE_CODE_EXISTS = 1008;

    /** 部门下存在子部门 */
    int DEPT_HAS_CHILDREN = 1009;

    /** 部门下存在用户 */
    int DEPT_HAS_USERS = 1010;

    /** 角色下存在用户 */
    int ROLE_HAS_USERS = 1011;

    /** 原密码错误 */
    int OLD_PASSWORD_ERROR = 1012;

    /** 数据权限不足 */
    int DATA_SCOPE_ERROR = 1013;

    /** 权限编码已存在 */
    int PERMISSION_CODE_EXISTS = 1014;

    /** 存在子权限，无法删除 */
    int PERMISSION_HAS_CHILDREN = 1015;

    /** 产品不存在 */
    int PRODUCT_NOT_FOUND = 2001;

    /** 产品编码已存在 */
    int PRODUCT_CODE_EXISTS = 2002;

    /** 产品被BOM引用，不能删除 */
    int PRODUCT_REFERENCED_BY_BOM = 2003;

    /** 原材料不能添加BOM */
    int RAW_MATERIAL_NO_BOM = 2004;

    /** BOM循环引用 */
    int BOM_CIRCULAR_REFERENCE = 2005;

    /** 分类不存在 */
    int CATEGORY_NOT_FOUND = 2006;

    /** 分类下有子分类，不能删除 */
    int CATEGORY_HAS_CHILDREN = 2007;

    /** 分类下有产品，不能删除 */
    int CATEGORY_HAS_PRODUCTS = 2008;

    /** 客户不存在 */
    int CUSTOMER_NOT_FOUND = 3001;

    /** 客户编码已存在 */
    int CUSTOMER_CODE_EXISTS = 3002;

    /** 客户下有联系人，不能删除 */
    int CUSTOMER_HAS_CONTACTS = 3003;

    /** 上传文件不能为空 */
    int FILE_EMPTY = 4001;

    /** 不支持的文件类型 */
    int FILE_TYPE_NOT_ALLOWED = 4002;

    /** 文件大小超限 */
    int FILE_SIZE_EXCEEDED = 4003;

    /** 文件上传失败 */
    int FILE_UPLOAD_FAILED = 4004;

    /** 文件路径不合法 */
    int FILE_PATH_INVALID = 4005;

    // ==================== 工作流错误码 ====================

    /** 流程实例不存在 */
    int PROCESS_NOT_FOUND = 5001;

    /** 任务不存在或无权操作 */
    int TASK_NOT_FOUND = 5002;

    /** 驳回时必须填写审批意见 */
    int REJECT_COMMENT_REQUIRED = 5003;

    /** 不允许的状态流转 */
    int INVALID_STATUS_TRANSITION = 5004;

    // ==================== 生产模块错误码 ====================

    /** 车间编码已存在 */
    int WORKSHOP_CODE_EXISTS = 6001;

    /** 车间不存在 */
    int WORKSHOP_NOT_FOUND = 6002;

    /** 车间下存在班组，不能删除 */
    int WORKSHOP_HAS_TEAMS = 6003;

    /** 班组编码已存在 */
    int TEAM_CODE_EXISTS = 6004;

    /** 设备类型编码已存在 */
    int EQUIPMENT_TYPE_CODE_EXISTS = 6005;

    /** 设备类型已被引用，不能删除 */
    int EQUIPMENT_TYPE_IN_USE = 6006;

    /** 设备编码已存在 */
    int EQUIPMENT_CODE_EXISTS = 6007;

    /** 工艺路线不存在 */
    int PROCESS_ROUTE_NOT_FOUND = 6008;

    /** 工序步骤不存在 */
    int PROCESS_STEP_NOT_FOUND = 6009;

    /** 生产计划不存在 */
    int PLAN_NOT_FOUND = 6010;

    /** 计划编码已存在 */
    int PLAN_CODE_EXISTS = 6011;

    /** 非草稿状态不能删除 */
    int PLAN_CANNOT_DELETE = 6012;

    /** 工单不存在 */
    int WORK_ORDER_NOT_FOUND = 6013;

    /** 工单编号已存在 */
    int WORK_ORDER_NO_EXISTS = 6014;

    /** 工单状态不允许该操作 */
    int WORK_ORDER_STATUS_ERROR = 6015;

    /** 库存不足 */
    int STOCK_INSUFFICIENT = 6016;

    // ==================== 采购模块错误码 ====================

    /** 供应商编码已存在 */
    int SUPPLIER_CODE_EXISTS = 6020;

    /** 供应商不存在 */
    int SUPPLIER_NOT_FOUND = 6021;

    /** 采购申请不存在 */
    int PURCHASE_REQUEST_NOT_FOUND = 6022;

    /** 采购单不存在 */
    int PURCHASE_ORDER_NOT_FOUND = 6023;

    /** 采购单编号已存在 */
    int PURCHASE_ORDER_NO_EXISTS = 6024;

    /** 采购单状态不允许该操作 */
    int PURCHASE_ORDER_STATUS_ERROR = 6025;

    /** 入库数量超出采购数量 */
    int RECEIPT_QTY_EXCEEDED = 6026;

    /** 采购申请状态不允许该操作 */
    int PURCHASE_REQUEST_STATUS_ERROR = 6027;

    /** 采购申请编号已存在 */
    int PURCHASE_REQUEST_NO_EXISTS = 6028;

    // ==================== 质量模块错误码 ====================

    /** 检验标准编码已存在 */
    int STANDARD_CODE_EXISTS = 6030;

    /** 检验标准不存在 */
    int STANDARD_NOT_FOUND = 6031;

    /** 来料检验单不存在 */
    int IQ_INSPECTION_NOT_FOUND = 6032;

    /** 成品检验单不存在 */
    int OQ_INSPECTION_NOT_FOUND = 6033;

    /** 不合格品记录不存在 */
    int DEFECT_RECORD_NOT_FOUND = 6034;

    /** 检验未完成 */
    int INSPECTION_NOT_COMPLETED = 6035;

    /** 检验结果不合格 */
    int INSPECTION_RESULT_FAILED = 6036;

    /** 不合格品记录状态不允许该操作 */
    int DEFECT_RECORD_STATUS_ERROR = 6037;

    // ==================== 销售发货/退货错误码 ====================

    /** 发货单不存在 */
    int DELIVERY_NOT_FOUND = 6040;

    /** 发货单号已存在 */
    int DELIVERY_NO_EXISTS = 6041;

    /** 发货数量超出订单数量 */
    int DELIVERY_QTY_EXCEEDED = 6042;

    /** 发货单状态不允许该操作 */
    int DELIVERY_STATUS_ERROR = 6043;

    /** 退货单不存在 */
    int RETURN_NOT_FOUND = 6044;

    /** 退货单号已存在 */
    int RETURN_NO_EXISTS = 6045;

    /** 退货数量超出发货数量 */
    int RETURN_QTY_EXCEEDED = 6046;

    /** 退货单状态不允许该操作 */
    int RETURN_STATUS_ERROR = 6047;

    // ==================== 财务模块错误码 ====================

    /** 应收单不存在 */
    int AR_NOT_FOUND = 6050;

    /** 应收单号已存在 */
    int AR_NO_EXISTS = 6051;

    /** 收款金额超出应收金额 */
    int AR_PAYMENT_EXCEEDED = 6052;

    /** 应付单不存在 */
    int AP_NOT_FOUND = 6053;

    /** 应付单号已存在 */
    int AP_NO_EXISTS = 6054;

    /** 付款金额超出应付金额 */
    int AP_PAYMENT_EXCEEDED = 6055;
}
