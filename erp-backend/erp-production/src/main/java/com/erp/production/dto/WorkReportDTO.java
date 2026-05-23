package com.erp.production.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkReportDTO {
    @NotNull(message = "工单不能为空")
    private Long workOrderId;
    @NotNull(message = "工序序号不能为空")
    private Integer stepNo;
    private String stepName;
    @NotNull(message = "报工数量不能为空")
    @DecimalMin(value = "0.01", message = "报工数量必须大于0")
    private BigDecimal reportQty;
    private BigDecimal actualHours;
    private String remark;
}
