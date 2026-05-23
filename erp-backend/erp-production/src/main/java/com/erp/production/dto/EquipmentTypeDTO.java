package com.erp.production.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EquipmentTypeDTO {
    @NotBlank(message = "类型编码不能为空")
    private String typeCode;
    @NotBlank(message = "类型名称不能为空")
    private String typeName;
    private String description;
}
