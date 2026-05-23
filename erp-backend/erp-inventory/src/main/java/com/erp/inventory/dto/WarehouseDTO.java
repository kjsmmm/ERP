package com.erp.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 仓库DTO
 */
@Data
public class WarehouseDTO {

    @NotBlank(message = "仓库编码不能为空")
    private String warehouseCode;

    @NotBlank(message = "仓库名称不能为空")
    private String warehouseName;

    private String address;
    private String manager;
    private String phone;
    private String remark;
}
