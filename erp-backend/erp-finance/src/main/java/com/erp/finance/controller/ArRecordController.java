package com.erp.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.finance.entity.ArRecord;
import com.erp.finance.service.ArRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Tag(name = "应收账款")
@RestController
@RequestMapping("/ar-records")
@RequiredArgsConstructor
public class ArRecordController {

    private final ArRecordService arRecordService;

    @Operation(summary = "应收单分页查询")
    @GetMapping
    public Result<PageResult<ArRecord>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<ArRecord> page = arRecordService.getArRecordPage(keyword, status, customerId, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "登记发票")
    @PutMapping("/{id}/invoice")
    public Result<Void> updateInvoice(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String invoiceNo = body.get("invoiceNo");
        LocalDate invoiceDate = body.get("invoiceDate") != null ? LocalDate.parse(body.get("invoiceDate")) : null;
        arRecordService.updateInvoice(id, invoiceNo, invoiceDate);
        return Result.success();
    }

    @Operation(summary = "收款核销")
    @PostMapping("/{id}/payment")
    public Result<Void> addPayment(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String paymentMethod = (String) body.get("paymentMethod");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        LocalDate paymentDate = body.get("paymentDate") != null ? LocalDate.parse(body.get("paymentDate").toString()) : null;
        String remark = (String) body.get("remark");
        arRecordService.addPayment(id, paymentMethod, amount, paymentDate, remark);
        return Result.success();
    }
}
