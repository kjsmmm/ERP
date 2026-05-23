package com.erp.purchase.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseReceiptDTO {
    @NotNull(message = "采购单不能为空")
    private Long purchaseOrderId;
    @NotNull(message = "仓库不能为空")
    private Long warehouseId;
    private String remark;
    @NotEmpty(message = "入库明细不能为空")
    @Valid
    private List<PurchaseReceiptItemDTO> items;

    @Data
    public static class PurchaseReceiptItemDTO {
        @NotNull(message = "产品不能为空")
        private Long productId;
        @NotNull(message = "数量不能为空")
        private Integer quantity;
        private String unit;
        private String remark;
    }
}
