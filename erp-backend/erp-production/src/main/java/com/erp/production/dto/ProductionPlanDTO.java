package com.erp.production.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductionPlanDTO {
    @NotBlank(message = "计划编码不能为空")
    private String planCode;
    private Long orderId;
    @NotNull(message = "产品不能为空")
    private Long productId;
    @NotNull(message = "计划数量不能为空")
    private BigDecimal plannedQty;
    private LocalDate startDate;
    private LocalDate endDate;
    private String remark;
}
