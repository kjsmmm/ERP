package com.erp.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.system.dto.LogQueryDTO;
import com.erp.system.entity.SysLog;
import com.erp.system.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志", description = "操作日志查询")
@RestController
@RequestMapping("/system/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:log:query')")
    public Result<IPage<SysLog>> page(LogQueryDTO queryDTO) {
        IPage<SysLog> page = logService.getLogPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取日志详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:log:query')")
    public Result<SysLog> getById(@PathVariable Long id) {
        SysLog log = logService.getById(id);
        return Result.success(log);
    }

    @Operation(summary = "删除日志")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:log:delete')")
    @Log(module = "操作日志", operation = "删除日志")
    public Result<Void> delete(@PathVariable Long id) {
        logService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "清空日志")
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('system:log:delete')")
    @Log(module = "操作日志", operation = "清空日志")
    public Result<Void> clear() {
        logService.remove(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        return Result.success();
    }
}
