package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("production_plan")
public class ProductionPlan extends BaseEntity {
    private String planCode;
    private Long orderId;
    private Long productId;
    private BigDecimal plannedQty;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String orderNo;
}
