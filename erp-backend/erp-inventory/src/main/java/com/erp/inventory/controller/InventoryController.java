package com.erp.inventory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.inventory.dto.InventoryQueryDTO;
import com.erp.inventory.entity.Inventory;
import com.erp.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "库存管理")
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "库存分页查询")
    @GetMapping
    public Result<PageResult<Inventory>> page(InventoryQueryDTO queryDTO) {
        IPage<Inventory> page = inventoryService.getInventoryPage(queryDTO);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "按产品查库存")
    @GetMapping("/product/{productId}")
    public Result<List<Inventory>> getByProduct(@PathVariable Long productId) {
        return Result.success(inventoryService.getByProductId(productId));
    }

    @Operation(summary = "库存预警查询")
    @GetMapping("/alert")
    public Result<List<Inventory>> getAlertList() {
        return Result.success(inventoryService.getAlertList());
    }
}
