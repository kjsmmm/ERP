package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("work_order")
public class WorkOrder extends BaseEntity {
    private String orderNo;
    private Long planId;
    private Long productId;
    private Long workshopId;
    private Long routeId;
    private String routeName;
    private BigDecimal plannedQty;
    private BigDecimal actualQty;
    private Integer status;
    private LocalDate startDate;
    private LocalDate endDate;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String workshopName;

    @TableField(exist = false)
    private List<WorkOrderStep> steps;
}
