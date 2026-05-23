package com.erp.product.controller;

import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.product.dto.BomItemDTO;
import com.erp.product.entity.BomItem;
import com.erp.product.service.BomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BOM管理控制器
 */
@Tag(name = "BOM管理", description = "BOM物料清单管理")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class BomController {

    private final BomService bomService;

    @Operation(summary = "获取产品BOM")
    @GetMapping("/{id}/bom")
    @PreAuthorize("hasAuthority('product:view')")
    public Result<List<BomItem>> getBom(@PathVariable Long id) {
        List<BomItem> items = bomService.getBomByProductId(id);
        return Result.success(items);
    }

    @Operation(summary = "更新产品BOM")
    @PutMapping("/{id}/bom")
    @PreAuthorize("hasAuthority('product:edit')")
    @Log(module = "BOM管理", operation = "更新BOM")
    public Result<Void> updateBom(@PathVariable Long id, @Valid @RequestBody List<BomItemDTO> items) {
        bomService.updateBom(id, items);
        return Result.success();
    }

    @Operation(summary = "递归展开BOM树")
    @GetMapping("/{id}/bom/expand")
    @PreAuthorize("hasAuthority('product:view')")
    public Result<List<BomItem>> expandBom(@PathVariable Long id) {
        List<BomItem> items = bomService.expandBomTree(id);
        return Result.success(items);
    }
}
