package com.erp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.production.dto.WorkshopDTO;
import com.erp.production.entity.Workshop;
import com.erp.production.service.WorkshopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "车间管理")
@RestController
@RequestMapping("/workshops")
@RequiredArgsConstructor
public class WorkshopController {

    private final WorkshopService workshopService;

    @Operation(summary = "车间分页查询")
    @GetMapping
    public Result<PageResult<Workshop>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Workshop> page = workshopService.getWorkshopPage(keyword, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建车间")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody WorkshopDTO dto) {
        workshopService.createWorkshop(dto);
        return Result.success();
    }

    @Operation(summary = "更新车间")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody WorkshopDTO dto) {
        workshopService.updateWorkshop(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除车间")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        workshopService.deleteWorkshop(id);
        return Result.success();
    }
}
