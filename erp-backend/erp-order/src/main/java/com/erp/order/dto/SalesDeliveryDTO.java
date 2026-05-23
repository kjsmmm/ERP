package com.erp.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SalesDeliveryDTO {

    @NotNull(message = "销售订单ID不能为空")
    private Long orderId;

    private Long customerId;
    private LocalDate deliveryDate;
    private String logisticsCompany;
    private String trackingNo;
    private Long warehouseId;
    private String remark;

    @NotNull(message = "发货明细不能为空")
    private List<DeliveryItemDTO> items;

    @Data
    public static class DeliveryItemDTO {
        @NotNull(message = "产品ID不能为空")
        private Long productId;

        @NotNull(message = "发货数量不能为空")
        private Integer quantity;
    }
}
