package com.erp.quality.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.quality.dto.QualityStandardDTO;
import com.erp.quality.entity.QualityStandard;
import com.erp.quality.service.QualityStandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "检验标准管理")
@RestController
@RequestMapping("/quality-standards")
@RequiredArgsConstructor
public class QualityStandardController {

    private final QualityStandardService qualityStandardService;

    @Operation(summary = "检验标准分页查询")
    @GetMapping
    public Result<PageResult<QualityStandard>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer applicableType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<QualityStandard> page = qualityStandardService.getStandardPage(keyword, applicableType, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建检验标准")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody QualityStandardDTO dto) {
        qualityStandardService.createStandard(dto);
        return Result.success();
    }

    @Operation(summary = "更新检验标准")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody QualityStandardDTO dto) {
        qualityStandardService.updateStandard(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除检验标准")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        qualityStandardService.deleteStandard(id);
        return Result.success();
    }
}
