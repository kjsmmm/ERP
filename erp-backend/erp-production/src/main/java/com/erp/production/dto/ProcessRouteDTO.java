package com.erp.production.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProcessRouteDTO {
    @NotNull(message = "产品不能为空")
    private Long productId;
    @NotBlank(message = "路线编码不能为空")
    private String routeCode;
    @NotBlank(message = "路线名称不能为空")
    private String routeName;
    private Integer isDefault;
    @Size(min = 1, message = "至少包含一个工序步骤")
    private List<ProcessStepDTO> steps;
}
