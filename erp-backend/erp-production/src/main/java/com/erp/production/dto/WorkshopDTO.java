package com.erp.production.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkshopDTO {
    @NotBlank(message = "车间编码不能为空")
    private String workshopCode;
    @NotBlank(message = "车间名称不能为空")
    private String workshopName;
    private String address;
    private String manager;
    private String phone;
    private String description;
}
