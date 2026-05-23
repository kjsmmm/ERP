package com.erp.inventory.dto;

import jakarta.validation.constraints.Max;
import lombok.Data;

/**
 * 库存查询DTO
 */
@Data
public class InventoryQueryDTO {

    private Integer pageNum = 1;

    @Max(value = 100, message = "每页最多100条")
    private Integer pageSize = 10;

    private Long productId;
    private Long warehouseId;
    private String keyword;
}
