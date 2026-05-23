package com.erp.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_item")
public class OrderItem extends BaseEntity {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 小计金额
     */
    private BigDecimal subtotal;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 产品名称（非数据库字段）
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 产品编码（非数据库字段）
     */
    @TableField(exist = false)
    private String productCode;

    /**
     * 产品单位（非数据库字段）
     */
    @TableField(exist = false)
    private String unit;

    /**
     * 产品规格（非数据库字段）
     */
    @TableField(exist = false)
    private String spec;
}
