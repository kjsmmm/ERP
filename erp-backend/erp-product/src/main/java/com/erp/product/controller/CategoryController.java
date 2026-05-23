package com.erp.product.controller;

import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.product.dto.CategoryDTO;
import com.erp.product.entity.ProductCategory;
import com.erp.product.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品分类控制器
 */
@Tag(name = "产品分类管理", description = "产品分类CRUD操作")
@RestController
@RequestMapping("/product/category")
@RequiredArgsConstructor
public class CategoryController {

    private final ProductCategoryService categoryService;

    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('product:view')")
    public Result<List<ProductCategory>> tree() {
        List<ProductCategory> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }

    @Operation(summary = "创建分类")
    @PostMapping
    @PreAuthorize("hasAuthority('product:category:add')")
    @Log(module = "产品分类", operation = "创建分类")
    public Result<Long> create(@Valid @RequestBody CategoryDTO dto) {
        Long categoryId = categoryService.createCategory(dto);
        return Result.success(categoryId);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:category:edit')")
    @Log(module = "产品分类", operation = "更新分类")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        categoryService.updateCategory(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:category:delete')")
    @Log(module = "产品分类", operation = "删除分类")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
