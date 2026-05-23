package com.erp.quality.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.quality.dto.DefectRecordDTO;
import com.erp.quality.entity.DefectRecord;
import com.erp.quality.service.DefectRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "不合格品处理")
@RestController
@RequestMapping("/defect-records")
@RequiredArgsConstructor
public class DefectRecordController {

    private final DefectRecordService defectRecordService;

    @Operation(summary = "不合格品记录分页查询")
    @GetMapping
    public Result<PageResult<DefectRecord>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer handleType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<DefectRecord> page = defectRecordService.getDefectRecordPage(keyword, handleType, status, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建不合格品记录")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody DefectRecordDTO dto) {
        defectRecordService.createRecord(dto);
        return Result.success();
    }

    @Operation(summary = "提交审批")
    @PostMapping("/{id}/submit")
    public Result<Void> submit(@PathVariable Long id) {
        defectRecordService.submitForApproval(id);
        return Result.success();
    }
}
