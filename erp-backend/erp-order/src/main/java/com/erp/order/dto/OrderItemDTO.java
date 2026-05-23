package com.erp.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细DTO
 */
@Data
public class OrderItemDTO {

    /**
     * 产品ID
     */
    @NotNull(message = "产品不能为空")
    private Long productId;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.0001", message = "数量必须大于0")
    private BigDecimal quantity;

    /**
     * 单价（为空时自动取客户专属价格或产品标准售价）
     */
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal unitPrice;

    /**
     * 排序
     */
    private Integer sortOrder;
}
