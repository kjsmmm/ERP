package com.erp.quality.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OqInspectionDTO {
    @NotNull(message = "工单不能为空")
    private Long workOrderId;
    private String remark;
    @Valid
    private List<OqInspectionItemDTO> items;

    @Data
    public static class OqInspectionItemDTO {
        private String itemName;
        private String inspectionMethod;
        private String standardValue;
        private String actualValue;
        private Integer judgment;
    }
}
