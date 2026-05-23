package com.erp.inventory.controller;

import com.erp.common.result.Result;
import com.erp.inventory.dto.WarehouseDTO;
import com.erp.inventory.entity.Warehouse;
import com.erp.inventory.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "仓库管理")
@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Operation(summary = "创建仓库")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody WarehouseDTO dto) {
        return Result.success(warehouseService.createWarehouse(dto));
    }

    @Operation(summary = "更新仓库")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody WarehouseDTO dto) {
        warehouseService.updateWarehouse(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除仓库")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return Result.success();
    }

    @Operation(summary = "仓库列表")
    @GetMapping
    public Result<List<Warehouse>> list() {
        return Result.success(warehouseService.listActive());
    }
}
