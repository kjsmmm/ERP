package com.erp.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalesReturnDTO {

    @NotNull(message = "发货单ID不能为空")
    private Long deliveryId;

    private String returnReason;
    private Long warehouseId;
    private String remark;

    @NotNull(message = "退货明细不能为空")
    private List<ReturnItemDTO> items;

    @Data
    public static class ReturnItemDTO {
        @NotNull(message = "产品ID不能为空")
        private Long productId;

        @NotNull(message = "退货数量不能为空")
        private Integer quantity;

        private String reason;
    }
}
