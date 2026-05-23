package com.erp.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * BOM项DTO
 */
@Data
public class BomItemDTO {

    /**
     * 子物料ID
     */
    @NotNull(message = "物料不能为空")
    private Long materialId;

    /**
     * 用量
     */
    @NotNull(message = "用量不能为空")
    @DecimalMin(value = "0.0001", message = "用量必须大于0")
    private BigDecimal quantity;

    /**
     * 损耗率(%)
     */
    private BigDecimal wasteRate;

    /**
     * 排序
     */
    private Integer sortOrder;
}
