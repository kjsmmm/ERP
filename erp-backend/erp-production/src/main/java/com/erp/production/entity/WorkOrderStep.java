package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("work_order_step")
public class WorkOrderStep extends BaseEntity {
    private Long workOrderId;
    private Integer stepNo;
    private String stepName;
    private BigDecimal standardTime;
    private String equipmentType;
    private String description;
}
