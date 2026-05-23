package com.erp.production.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EquipmentDTO {
    @NotBlank(message = "设备编码不能为空")
    private String equipmentCode;
    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;
    @NotNull(message = "设备类型不能为空")
    private Long equipmentTypeId;
    @NotNull(message = "所属车间不能为空")
    private Long workshopId;
    private LocalDate purchaseDate;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private String remark;
}
