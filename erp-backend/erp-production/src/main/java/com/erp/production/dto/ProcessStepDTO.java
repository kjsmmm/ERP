package com.erp.production.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessStepDTO {
    @NotNull(message = "步骤序号不能为空")
    private Integer stepNo;
    @NotBlank(message = "步骤名称不能为空")
    private String stepName;
    private BigDecimal standardTime;
    private String equipmentType;
    private String description;
}
