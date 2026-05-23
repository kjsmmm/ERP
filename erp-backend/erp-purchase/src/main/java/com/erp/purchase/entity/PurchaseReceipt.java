package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_receipt")
public class PurchaseReceipt extends BaseEntity {

    private String receiptNo;
    private Long purchaseOrderId;
    private Long warehouseId;
    private Integer status;
    private Integer inspectionStatus;
    private String remark;
}
