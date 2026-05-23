package com.erp.system.controller;

import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.system.entity.SysPermission;
import com.erp.system.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 */
@Tag(name = "权限管理", description = "权限树CRUD操作")
@RestController
@RequestMapping("/system/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "获取权限树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:permission:view')")
    public Result<List<SysPermission>> tree() {
        List<SysPermission> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }

    @Operation(summary = "获取权限详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:view')")
    public Result<SysPermission> getById(@PathVariable Long id) {
        SysPermission permission = permissionService.getById(id);
        return Result.success(permission);
    }

    @Operation(summary = "创建权限")
    @PostMapping
    @PreAuthorize("hasAuthority('system:permission:add')")
    @Log(module = "权限管理", operation = "创建权限")
    public Result<Long> create(@Valid @RequestBody SysPermission permission) {
        Long permId = permissionService.createPermission(permission);
        return Result.success(permId);
    }

    @Operation(summary = "更新权限")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:edit')")
    @Log(module = "权限管理", operation = "更新权限")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysPermission permission) {
        permissionService.updatePermission(id, permission);
        return Result.success();
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    @Log(module = "权限管理", operation = "删除权限")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }

    @Operation(summary = "获取角色的权限ID列表")
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('system:permission:view')")
    public Result<List<Long>> getPermissionIdsByRoleId(@PathVariable Long roleId) {
        List<Long> permissionIds = permissionService.getPermissionIdsByRoleId(roleId);
        return Result.success(permissionIds);
    }
}
