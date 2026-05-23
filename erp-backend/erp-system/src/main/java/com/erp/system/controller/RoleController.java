package com.erp.system.controller;

import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.system.entity.SysRole;
import com.erp.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Tag(name = "角色管理", description = "角色CRUD操作")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "获取所有角色")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role')")
    public Result<List<SysRole>> list() {
        List<SysRole> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role')")
    public Result<SysRole> getById(@PathVariable Long id) {
        SysRole role = roleService.getById(id);
        return Result.success(role);
    }

    @Operation(summary = "创建角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    @Log(module = "角色管理", operation = "创建角色")
    public Result<Long> create(@Valid @RequestBody SysRole role) {
        Long roleId = roleService.createRole(role);
        return Result.success(roleId);
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @Log(module = "角色管理", operation = "更新角色")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysRole role) {
        roleService.updateRole(id, role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    @Log(module = "角色管理", operation = "删除角色")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @Operation(summary = "分配权限")
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @Log(module = "角色管理", operation = "分配权限")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.success();
    }
}
