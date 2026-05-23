package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("process_step")
public class ProcessStep extends BaseEntity {
    private Long routeId;
    private Integer stepNo;
    private String stepName;
    private BigDecimal standardTime;
    private String equipmentType;
    private String description;
}
