package com.erp.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 出库DTO
 */
@Data
public class StockOutDTO {

    @NotNull(message = "产品不能为空")
    private Long productId;

    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.0001", message = "数量必须大于0")
    private BigDecimal quantity;

    private String referenceNo;
    private String referenceType;
    private Long referenceId;
    private String remark;
}
