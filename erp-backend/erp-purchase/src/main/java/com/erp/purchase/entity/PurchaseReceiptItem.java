package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_receipt_item")
public class PurchaseReceiptItem extends BaseEntity {

    private Long purchaseReceiptId;
    private Long productId;
    private Integer quantity;
    private String unit;
    private String remark;
}
