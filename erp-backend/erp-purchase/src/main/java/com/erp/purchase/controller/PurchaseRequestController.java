package com.erp.purchase.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.purchase.dto.PurchaseRequestDTO;
import com.erp.purchase.entity.PurchaseRequest;
import com.erp.purchase.service.PurchaseRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "采购申请")
@RestController
@RequestMapping("/purchase-requests")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    @Operation(summary = "采购申请分页查询")
    @GetMapping
    public Result<PageResult<PurchaseRequest>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<PurchaseRequest> page = purchaseRequestService.getPurchaseRequestPage(keyword, status, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建采购申请")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody PurchaseRequestDTO dto) {
        purchaseRequestService.createPurchaseRequest(dto);
        return Result.success();
    }

    @Operation(summary = "提交审批")
    @PostMapping("/{id}/submit")
    public Result<Void> submit(@PathVariable Long id) {
        purchaseRequestService.submitForApproval(id);
        return Result.success();
    }
}
