package com.erp.purchase.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseRequestDTO {
    @NotNull(message = "申请类型不能为空")
    private Integer requestType;
    private String remark;
    @NotEmpty(message = "申请明细不能为空")
    @Valid
    private List<PurchaseRequestItemDTO> items;

    @Data
    public static class PurchaseRequestItemDTO {
        @NotNull(message = "产品不能为空")
        private Long productId;
        @NotNull(message = "数量不能为空")
        private Integer quantity;
        private String unit;
        private String remark;
    }
}
