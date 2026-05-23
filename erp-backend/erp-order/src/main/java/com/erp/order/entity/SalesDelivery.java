package com.erp.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_delivery")
public class SalesDelivery extends BaseEntity {

    private String deliveryNo;
    private Long orderId;
    private Long customerId;
    private LocalDate deliveryDate;
    private String logisticsCompany;
    private String trackingNo;
    private Long warehouseId;
    private Integer status;
    private String remark;

    @TableField(exist = false)
    private List<SalesDeliveryItem> items;

    @TableField(exist = false)
    private String customerName;

    @TableField(exist = false)
    private String orderNo;
}
