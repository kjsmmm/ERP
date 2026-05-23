package com.erp.production.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WorkOrderDTO {
    @NotBlank(message = "工单编号不能为空")
    private String orderNo;
    private Long planId;
    @NotNull(message = "产品不能为空")
    private Long productId;
    @NotNull(message = "车间不能为空")
    private Long workshopId;
    @NotNull(message = "工艺路线不能为空")
    private Long routeId;
    @NotNull(message = "计划数量不能为空")
    private BigDecimal plannedQty;
    private LocalDate startDate;
    private LocalDate endDate;
    private String remark;
}
