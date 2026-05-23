package com.erp.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.system.dto.UserDTO;
import com.erp.system.dto.UserQueryDTO;
import com.erp.system.entity.SysUser;
import com.erp.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户CRUD操作")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user')")
    public Result<IPage<SysUser>> page(UserQueryDTO queryDTO) {
        IPage<SysUser> page = userService.getUserPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user')")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        return Result.success(user);
    }

    @Operation(summary = "创建用户")
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    @Log(module = "用户管理", operation = "创建用户")
    public Result<Long> create(@Valid @RequestBody UserDTO userDTO) {
        Long userId = userService.createUser(userDTO);
        return Result.success(userId);
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Log(module = "用户管理", operation = "更新用户")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        userService.updateUser(id, userDTO);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    @Log(module = "用户管理", operation = "删除用户")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('system:user:resetpwd')")
    @Log(module = "用户管理", operation = "重置密码")
    public Result<String> resetPassword(@PathVariable Long id) {
        String newPassword = userService.resetPassword(id);
        return Result.success(newPassword);
    }

    @Operation(summary = "修改状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Log(module = "用户管理", operation = "修改状态")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.changeStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PutMapping("/{id}/password")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Log(module = "用户管理", operation = "修改密码")
    public Result<Void> changePassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        userService.changePassword(id, oldPassword, newPassword);
        return Result.success();
    }
}
