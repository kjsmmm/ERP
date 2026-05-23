package com.erp.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 客户产品专属价格
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_product_price")
public class CustomerProductPrice extends BaseEntity {

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 客户专属价格
     */
    private BigDecimal price;

    /**
     * 备注
     */
    private String remark;
}
