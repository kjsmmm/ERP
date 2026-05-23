package com.erp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.production.dto.ProductionPlanDTO;
import com.erp.production.entity.ProductionPlan;
import com.erp.production.service.ProductionPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "生产计划管理")
@RestController
@RequestMapping("/production-plans")
@RequiredArgsConstructor
public class ProductionPlanController {

    private final ProductionPlanService productionPlanService;

    @Operation(summary = "计划分页查询")
    @GetMapping
    public Result<PageResult<ProductionPlan>> page(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<ProductionPlan> page = productionPlanService.getPlanPage(status, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建计划")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ProductionPlanDTO dto) {
        productionPlanService.createPlan(dto);
        return Result.success();
    }

    @Operation(summary = "更新计划")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductionPlanDTO dto) {
        productionPlanService.updatePlan(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除计划")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productionPlanService.deletePlan(id);
        return Result.success();
    }

    @Operation(summary = "下达计划")
    @PutMapping("/{id}/release")
    public Result<Void> release(@PathVariable Long id) {
        productionPlanService.release(id);
        return Result.success();
    }

    @Operation(summary = "开始执行")
    @PutMapping("/{id}/start")
    public Result<Void> start(@PathVariable Long id) {
        productionPlanService.start(id);
        return Result.success();
    }

    @Operation(summary = "完成计划")
    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        productionPlanService.complete(id);
        return Result.success();
    }
}
