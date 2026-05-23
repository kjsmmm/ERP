package com.erp.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.customer.dto.FollowDTO;
import com.erp.customer.entity.CustomerFollow;
import com.erp.customer.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 客户跟进记录控制器
 */
@Tag(name = "客户跟进记录", description = "跟进记录CRUD操作")
@RestController
@RequestMapping("/customer/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "分页查询跟进记录")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('customer:view')")
    public Result<IPage<CustomerFollow>> page(
            @RequestParam Long customerId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<CustomerFollow> page = followService.getFollowPage(customerId, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "创建跟进记录")
    @PostMapping
    @PreAuthorize("hasAuthority('customer:edit')")
    @Log(module = "客户跟进记录", operation = "创建跟进记录")
    public Result<Long> create(@Valid @RequestBody FollowDTO dto) {
        Long followId = followService.createFollow(dto);
        return Result.success(followId);
    }

    @Operation(summary = "删除跟进记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:edit')")
    @Log(module = "客户跟进记录", operation = "删除跟进记录")
    public Result<Void> delete(@PathVariable Long id) {
        followService.deleteFollow(id);
        return Result.success();
    }
}
