package com.erp.production.controller;

import com.erp.common.result.Result;
import com.erp.production.dto.WorkReportDTO;
import com.erp.production.entity.WorkReport;
import com.erp.production.service.WorkReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "报工管理")
@RestController
@RequestMapping("/work-reports")
@RequiredArgsConstructor
public class WorkReportController {

    private final WorkReportService workReportService;

    @Operation(summary = "创建报工")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody WorkReportDTO dto) {
        workReportService.createReport(dto);
        return Result.success();
    }

    @Operation(summary = "按工单查询报工记录")
    @GetMapping("/order/{workOrderId}")
    public Result<List<WorkReport>> getByOrderId(@PathVariable Long workOrderId) {
        return Result.success(workReportService.getByOrderId(workOrderId));
    }
}
