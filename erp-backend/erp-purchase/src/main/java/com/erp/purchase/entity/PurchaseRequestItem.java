package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_request_item")
public class PurchaseRequestItem extends BaseEntity {

    private Long purchaseRequestId;
    private Long productId;
    private Integer quantity;
    private String unit;
    private String remark;
}
