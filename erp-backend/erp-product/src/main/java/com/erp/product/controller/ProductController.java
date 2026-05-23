package com.erp.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.product.dto.ProductDTO;
import com.erp.product.dto.ProductQueryDTO;
import com.erp.product.entity.Product;
import com.erp.product.service.ProductService;
import com.erp.product.vo.ProductDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 产品管理控制器
 */
@Tag(name = "产品管理", description = "产品CRUD操作")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "分页查询产品")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('product:view')")
    public Result<IPage<Product>> page(ProductQueryDTO queryDTO) {
        IPage<Product> page = productService.getProductPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取产品详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('product:view')")
    public Result<ProductDetailVO> getById(@PathVariable Long id) {
        ProductDetailVO detail = productService.getProductDetail(id);
        return Result.success(detail);
    }

    @Operation(summary = "创建产品")
    @PostMapping
    @PreAuthorize("hasAuthority('product:add')")
    @Log(module = "产品管理", operation = "创建产品")
    public Result<Long> create(@Valid @RequestBody ProductDTO dto) {
        Long productId = productService.createProduct(dto);
        return Result.success(productId);
    }

    @Operation(summary = "更新产品")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:edit')")
    @Log(module = "产品管理", operation = "更新产品")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        productService.updateProduct(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除产品")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    @Log(module = "产品管理", operation = "删除产品")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @Operation(summary = "修改产品状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('product:edit')")
    @Log(module = "产品管理", operation = "修改产品状态")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.changeStatus(id, status);
        return Result.success();
    }
}
