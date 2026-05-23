package com.erp.purchase.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.purchase.dto.PurchaseOrderDTO;
import com.erp.purchase.entity.PurchaseOrder;
import com.erp.purchase.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "采购单")
@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Operation(summary = "采购单分页查询")
    @GetMapping
    public Result<PageResult<PurchaseOrder>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<PurchaseOrder> page = purchaseOrderService.getPurchaseOrderPage(keyword, status, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建采购单")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody PurchaseOrderDTO dto) {
        purchaseOrderService.createPurchaseOrder(dto);
        return Result.success();
    }

    @Operation(summary = "确认采购单")
    @PostMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        purchaseOrderService.confirmOrder(id);
        return Result.success();
    }

    @Operation(summary = "取消采购单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        purchaseOrderService.cancelOrder(id);
        return Result.success();
    }
}
