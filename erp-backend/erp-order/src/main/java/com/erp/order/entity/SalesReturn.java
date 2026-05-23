package com.erp.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_return")
public class SalesReturn extends BaseEntity {

    private String returnNo;
    private Long deliveryId;
    private Long orderId;
    private Long customerId;
    private String returnReason;
    private Long warehouseId;
    private Integer status;
    private String processInstanceId;
    private String remark;

    @TableField(exist = false)
    private List<SalesReturnItem> items;

    @TableField(exist = false)
    private String customerName;

    @TableField(exist = false)
    private String deliveryNo;
}
