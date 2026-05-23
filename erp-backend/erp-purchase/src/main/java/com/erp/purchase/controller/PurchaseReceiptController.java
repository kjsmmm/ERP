package com.erp.purchase.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.purchase.dto.PurchaseReceiptDTO;
import com.erp.purchase.entity.PurchaseReceipt;
import com.erp.purchase.service.PurchaseReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "采购入库")
@RestController
@RequestMapping("/purchase-receipts")
@RequiredArgsConstructor
public class PurchaseReceiptController {

    private final PurchaseReceiptService purchaseReceiptService;

    @Operation(summary = "采购入库分页查询")
    @GetMapping
    public Result<PageResult<PurchaseReceipt>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<PurchaseReceipt> page = purchaseReceiptService.getPurchaseReceiptPage(keyword, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建采购入库")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody PurchaseReceiptDTO dto) {
        purchaseReceiptService.createReceipt(dto);
        return Result.success();
    }
}
