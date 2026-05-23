package com.erp.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.order.dto.SalesReturnDTO;
import com.erp.order.entity.SalesReturn;
import com.erp.order.service.SalesReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "销售退货")
@RestController
@RequestMapping("/sales-returns")
@RequiredArgsConstructor
public class SalesReturnController {

    private final SalesReturnService salesReturnService;

    @Operation(summary = "退货单分页查询")
    @GetMapping
    public Result<PageResult<SalesReturn>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<SalesReturn> page = salesReturnService.getReturnPage(keyword, status, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建退货单")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SalesReturnDTO dto) {
        salesReturnService.createReturn(dto);
        return Result.success();
    }

    @Operation(summary = "提交审批")
    @PostMapping("/{id}/submit")
    public Result<Void> submit(@PathVariable Long id) {
        salesReturnService.submitForApproval(id);
        return Result.success();
    }

    @Operation(summary = "退货入库")
    @PostMapping("/{id}/receive")
    public Result<Void> receive(@PathVariable Long id) {
        salesReturnService.receive(id);
        return Result.success();
    }
}
