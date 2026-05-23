package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("work_report")
public class WorkReport extends BaseEntity {
    private Long workOrderId;
    private Integer stepNo;
    private String stepName;
    private BigDecimal reportQty;
    private BigDecimal actualHours;
    private LocalDateTime reportTime;
    private Long reporterId;

    @TableField(exist = false)
    private String reporterName;
}
