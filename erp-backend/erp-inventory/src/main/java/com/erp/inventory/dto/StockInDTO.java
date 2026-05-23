package com.erp.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 入库DTO
 */
@Data
public class StockInDTO {

    @NotNull(message = "产品不能为空")
    private Long productId;

    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.0001", message = "数量必须大于0")
    private BigDecimal quantity;

    /** 关联单据号 */
    private String referenceNo;

    /** 关联单据类型 */
    private String referenceType;

    /** 关联单据ID */
    private Long referenceId;

    private String remark;
}
