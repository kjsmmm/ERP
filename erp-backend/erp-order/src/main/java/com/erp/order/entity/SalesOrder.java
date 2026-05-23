package com.erp.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 销售订单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_order")
public class SalesOrder extends BaseEntity {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 订单状态（1草稿 2已确认 3生产中 4已完成 5已关闭 6已取消 7已暂停 8变更审批中）
     */
    private Integer status;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 交货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    /**
     * 收货地址
     */
    private String deliveryAddress;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 订单明细（非数据库字段）
     */
    @TableField(exist = false)
    private List<OrderItem> items;

    /**
     * 客户名称（非数据库字段）
     */
    @TableField(exist = false)
    private String customerName;

    /**
     * 客户编码（非数据库字段）
     */
    @TableField(exist = false)
    private String customerCode;

    /**
     * 流程实例ID（审批流程）
     */
    private String processInstanceId;

    /**
     * 待审批变更数据（JSON格式）
     */
    private String pendingData;
}
