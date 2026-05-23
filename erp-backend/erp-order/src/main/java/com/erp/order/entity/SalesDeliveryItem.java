package com.erp.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_delivery_item")
public class SalesDeliveryItem extends BaseEntity {

    private Long deliveryId;
    private Long productId;
    private Integer quantity;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String productName;
}
