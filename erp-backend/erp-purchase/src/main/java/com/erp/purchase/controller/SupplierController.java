package com.erp.purchase.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.purchase.dto.SupplierDTO;
import com.erp.purchase.entity.Supplier;
import com.erp.purchase.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "供应商管理")
@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @Operation(summary = "供应商分页查询")
    @GetMapping
    public Result<PageResult<Supplier>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Supplier> page = supplierService.getSupplierPage(keyword, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "供应商列表（全部启用）")
    @GetMapping("/list")
    public Result<List<Supplier>> list() {
        return Result.success(supplierService.listAll());
    }

    @Operation(summary = "创建供应商")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SupplierDTO dto) {
        supplierService.createSupplier(dto);
        return Result.success();
    }

    @Operation(summary = "更新供应商")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SupplierDTO dto) {
        supplierService.updateSupplier(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除供应商")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return Result.success();
    }
}
