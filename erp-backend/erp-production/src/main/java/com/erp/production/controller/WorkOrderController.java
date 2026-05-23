package com.erp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.production.dto.WorkOrderDTO;
import com.erp.production.entity.WorkOrder;
import com.erp.production.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "工单管理")
@RestController
@RequestMapping("/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @Operation(summary = "工单分页查询")
    @GetMapping
    public Result<PageResult<WorkOrder>> page(
            @RequestParam(required = false) Long workshopId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<WorkOrder> page = workOrderService.getOrderPage(workshopId, status, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "工单详情")
    @GetMapping("/{id}")
    public Result<WorkOrder> detail(@PathVariable Long id) {
        return Result.success(workOrderService.getDetail(id));
    }

    @Operation(summary = "创建工单")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody WorkOrderDTO dto) {
        workOrderService.createOrder(dto);
        return Result.success();
    }

    @Operation(summary = "更新工单")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody WorkOrderDTO dto) {
        workOrderService.updateOrder(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除工单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        workOrderService.deleteOrder(id);
        return Result.success();
    }

    @Operation(summary = "下达工单")
    @PutMapping("/{id}/release")
    public Result<Void> release(@PathVariable Long id) {
        workOrderService.release(id);
        return Result.success();
    }

    @Operation(summary = "开始生产")
    @PutMapping("/{id}/start")
    public Result<Void> start(@PathVariable Long id) {
        workOrderService.start(id);
        return Result.success();
    }

    @Operation(summary = "完工")
    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id, @RequestParam BigDecimal actualQty) {
        workOrderService.complete(id, actualQty);
        return Result.success();
    }

    @Operation(summary = "关闭工单")
    @PutMapping("/{id}/close")
    public Result<Void> close(@PathVariable Long id) {
        workOrderService.close(id);
        return Result.success();
    }
}
