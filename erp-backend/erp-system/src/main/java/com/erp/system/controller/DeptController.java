package com.erp.system.controller;

import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.system.entity.SysDept;
import com.erp.system.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@Tag(name = "部门管理", description = "部门CRUD操作")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:dept')")
    public Result<List<SysDept>> tree() {
        List<SysDept> deptTree = deptService.getDeptTree();
        return Result.success(deptTree);
    }

    @Operation(summary = "获取部门详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept')")
    public Result<SysDept> getById(@PathVariable Long id) {
        SysDept dept = deptService.getById(id);
        return Result.success(dept);
    }

    @Operation(summary = "创建部门")
    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:add')")
    @Log(module = "部门管理", operation = "创建部门")
    public Result<Long> create(@Valid @RequestBody SysDept dept) {
        Long deptId = deptService.createDept(dept);
        return Result.success(deptId);
    }

    @Operation(summary = "更新部门")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @Log(module = "部门管理", operation = "更新部门")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysDept dept) {
        deptService.updateDept(id, dept);
        return Result.success();
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    @Log(module = "部门管理", operation = "删除部门")
    public Result<Void> delete(@PathVariable Long id) {
        deptService.deleteDept(id);
        return Result.success();
    }
}
