package com.erp.production.controller;

import com.erp.common.result.Result;
import com.erp.production.dto.ProcessRouteDTO;
import com.erp.production.entity.ProcessRoute;
import com.erp.production.service.ProcessRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "工艺路线管理")
@RestController
@RequestMapping("/process-routes")
@RequiredArgsConstructor
public class ProcessRouteController {

    private final ProcessRouteService processRouteService;

    @Operation(summary = "按产品查询工艺路线列表")
    @GetMapping("/product/{productId}")
    public Result<List<ProcessRoute>> getByProductId(@PathVariable Long productId) {
        return Result.success(processRouteService.getByProductId(productId));
    }

    @Operation(summary = "工艺路线详情")
    @GetMapping("/{id}")
    public Result<ProcessRoute> detail(@PathVariable Long id) {
        return Result.success(processRouteService.getDetail(id));
    }

    @Operation(summary = "创建工艺路线")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ProcessRouteDTO dto) {
        processRouteService.createRoute(dto);
        return Result.success();
    }

    @Operation(summary = "更新工艺路线")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProcessRouteDTO dto) {
        processRouteService.updateRoute(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除工艺路线")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        processRouteService.deleteRoute(id);
        return Result.success();
    }

    @Operation(summary = "设为默认工艺路线")
    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id) {
        processRouteService.setDefault(id);
        return Result.success();
    }
}
