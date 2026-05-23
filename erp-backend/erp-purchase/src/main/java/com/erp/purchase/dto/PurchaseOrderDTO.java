package com.erp.purchase.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseOrderDTO {
    @NotNull(message = "供应商不能为空")
    private Long supplierId;
    private Long purchaseRequestId;
    private String remark;
    @NotEmpty(message = "采购明细不能为空")
    @Valid
    private List<PurchaseOrderItemDTO> items;

    @Data
    public static class PurchaseOrderItemDTO {
        @NotNull(message = "产品不能为空")
        private Long productId;
        @NotNull(message = "数量不能为空")
        private Integer quantity;
        @NotNull(message = "单价不能为空")
        private BigDecimal unitPrice;
        private BigDecimal amount;
        private String unit;
        private String remark;
    }
}
