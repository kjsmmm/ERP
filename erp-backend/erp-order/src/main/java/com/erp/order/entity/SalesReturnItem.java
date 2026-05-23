package com.erp.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_return_item")
public class SalesReturnItem extends BaseEntity {

    private Long returnId;
    private Long productId;
    private Integer quantity;
    private String reason;

    @TableField(exist = false)
    private String productName;
}
