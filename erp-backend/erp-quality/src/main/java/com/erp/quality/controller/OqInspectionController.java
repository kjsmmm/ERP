package com.erp.quality.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.quality.dto.OqInspectionDTO;
import com.erp.quality.entity.OqInspection;
import com.erp.quality.service.OqInspectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "成品检验")
@RestController
@RequestMapping("/oq-inspections")
@RequiredArgsConstructor
public class OqInspectionController {

    private final OqInspectionService oqInspectionService;

    @Operation(summary = "成品检验分页查询")
    @GetMapping
    public Result<PageResult<OqInspection>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer inspectionResult,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<OqInspection> page = oqInspectionService.getOqInspectionPage(keyword, inspectionResult, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建成品检验单")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody OqInspectionDTO dto) {
        oqInspectionService.createInspection(dto);
        return Result.success();
    }

    @Operation(summary = "提交检验结果")
    @PostMapping("/{id}/submit")
    public Result<Void> submitResult(@PathVariable Long id, @Valid @RequestBody OqInspectionDTO dto) {
        oqInspectionService.submitResult(id, dto);
        return Result.success();
    }
}
