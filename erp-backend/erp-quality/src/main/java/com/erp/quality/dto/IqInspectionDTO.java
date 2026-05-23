package com.erp.quality.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class IqInspectionDTO {
    @NotNull(message = "采购单不能为空")
    private Long purchaseOrderId;
    private String remark;
    @Valid
    private List<IqInspectionItemDTO> items;

    @Data
    public static class IqInspectionItemDTO {
        private String itemName;
        private String inspectionMethod;
        private String standardValue;
        private String actualValue;
        private Integer judgment;
    }
}
