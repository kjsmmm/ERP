package com.erp.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.order.dto.OrderDTO;
import com.erp.order.dto.OrderQueryDTO;
import com.erp.order.entity.SalesOrder;
import com.erp.order.service.OrderService;
import com.erp.order.vo.OrderDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Tag(name = "订单管理", description = "销售订单CRUD及状态管理")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody OrderDTO dto) {
        return Result.success(orderService.createOrder(dto));
    }

    @Operation(summary = "订单分页查询")
    @GetMapping
    public Result<PageResult<SalesOrder>> page(OrderQueryDTO queryDTO) {
        IPage<SalesOrder> page = orderService.getOrderPage(queryDTO);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @Operation(summary = "更新订单")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody OrderDTO dto) {
        orderService.updateOrder(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return Result.success();
    }

    @Operation(summary = "确认订单")
    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }

    @Operation(summary = "完成订单")
    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        orderService.completeOrder(id);
        return Result.success();
    }

    @Operation(summary = "关闭订单")
    @PutMapping("/{id}/close")
    public Result<Void> close(@PathVariable Long id) {
        orderService.closeOrder(id);
        return Result.success();
    }
}
