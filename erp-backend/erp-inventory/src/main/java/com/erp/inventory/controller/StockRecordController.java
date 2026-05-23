package com.erp.inventory.controller;

import com.erp.common.result.Result;
import com.erp.inventory.dto.StockInDTO;
import com.erp.inventory.dto.StockOutDTO;
import com.erp.inventory.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "出入库管理")
@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockRecordController {

    private final StockService stockService;

    @Operation(summary = "入库")
    @PostMapping("/in")
    public Result<Void> stockIn(@Valid @RequestBody StockInDTO dto) {
        stockService.stockIn(dto);
        return Result.success();
    }

    @Operation(summary = "出库")
    @PostMapping("/out")
    public Result<Void> stockOut(@Valid @RequestBody StockOutDTO dto) {
        stockService.stockOut(dto);
        return Result.success();
    }
}
