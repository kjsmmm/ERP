package com.erp.product.controller;

import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.product.entity.ProductImage;
import com.erp.product.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 产品图片控制器
 */
@Tag(name = "产品图片管理", description = "产品图片上传和管理")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @Operation(summary = "获取产品图片列表")
    @GetMapping("/{id}/images")
    @PreAuthorize("hasAuthority('product:view')")
    public Result<List<ProductImage>> list(@PathVariable Long id) {
        List<ProductImage> images = productImageService.getImagesByProductId(id);
        return Result.success(images);
    }

    @Operation(summary = "上传产品图片")
    @PostMapping("/{id}/images")
    @PreAuthorize("hasAuthority('product:edit')")
    @Log(module = "产品管理", operation = "上传产品图片")
    public Result<Long> upload(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Long imageId = productImageService.uploadImage(id, file);
        return Result.success(imageId);
    }

    @Operation(summary = "设为主图")
    @PutMapping("/image/{id}/primary")
    @PreAuthorize("hasAuthority('product:edit')")
    @Log(module = "产品管理", operation = "设为主图")
    public Result<Void> setPrimary(@PathVariable Long id) {
        productImageService.setPrimary(id);
        return Result.success();
    }

    @Operation(summary = "删除图片")
    @DeleteMapping("/image/{id}")
    @PreAuthorize("hasAuthority('product:edit')")
    @Log(module = "产品管理", operation = "删除图片")
    public Result<Void> delete(@PathVariable Long id) {
        productImageService.deleteImage(id);
        return Result.success();
    }
}
