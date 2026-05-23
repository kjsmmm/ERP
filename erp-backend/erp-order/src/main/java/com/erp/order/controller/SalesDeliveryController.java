package com.erp.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.order.dto.SalesDeliveryDTO;
import com.erp.order.entity.SalesDelivery;
import com.erp.order.service.SalesDeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "销售发货")
@RestController
@RequestMapping("/sales-deliveries")
@RequiredArgsConstructor
public class SalesDeliveryController {

    private final SalesDeliveryService salesDeliveryService;

    @Operation(summary = "发货单分页查询")
    @GetMapping
    public Result<PageResult<SalesDelivery>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<SalesDelivery> page = salesDeliveryService.getDeliveryPage(keyword, status, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建发货单")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SalesDeliveryDTO dto) {
        salesDeliveryService.createDelivery(dto);
        return Result.success();
    }

    @Operation(summary = "拣货")
    @PostMapping("/{id}/pick")
    public Result<Void> pick(@PathVariable Long id) {
        salesDeliveryService.pick(id);
        return Result.success();
    }

    @Operation(summary = "出库确认")
    @PostMapping("/{id}/ship-out")
    public Result<Void> shipOut(@PathVariable Long id) {
        salesDeliveryService.shipOut(id);
        return Result.success();
    }

    @Operation(summary = "签收")
    @PostMapping("/{id}/sign")
    public Result<Void> sign(@PathVariable Long id) {
        salesDeliveryService.sign(id);
        return Result.success();
    }
}
