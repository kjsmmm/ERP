package com.erp.quality.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.quality.dto.IqInspectionDTO;
import com.erp.quality.entity.IqInspection;
import com.erp.quality.service.IqInspectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "来料检验")
@RestController
@RequestMapping("/iq-inspections")
@RequiredArgsConstructor
public class IqInspectionController {

    private final IqInspectionService iqInspectionService;

    @Operation(summary = "来料检验分页查询")
    @GetMapping
    public Result<PageResult<IqInspection>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer inspectionResult,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<IqInspection> page = iqInspectionService.getIqInspectionPage(keyword, inspectionResult, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建来料检验单")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody IqInspectionDTO dto) {
        iqInspectionService.createInspection(dto);
        return Result.success();
    }

    @Operation(summary = "提交检验结果")
    @PostMapping("/{id}/submit")
    public Result<Void> submitResult(@PathVariable Long id, @Valid @RequestBody IqInspectionDTO dto) {
        iqInspectionService.submitResult(id, dto);
        return Result.success();
    }
}
