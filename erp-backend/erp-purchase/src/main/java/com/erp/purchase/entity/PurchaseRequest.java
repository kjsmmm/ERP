package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_request")
public class PurchaseRequest extends BaseEntity {

    private String requestNo;
    private Integer requestType;
    private Integer status;
    private String processInstanceId;
    private String remark;
}
