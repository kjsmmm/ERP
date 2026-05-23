package com.erp.auth.controller;

import com.erp.auth.dto.LoginDTO;
import com.erp.auth.service.AuthService;
import com.erp.auth.vo.TokenVO;
import com.erp.auth.vo.UserInfoVO;
import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "登录、注销、Token刷新")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @Log(module = "认证管理", operation = "用户登录")
    @PostMapping("/login")
    public Result<TokenVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        TokenVO tokenVO = authService.login(loginDTO);
        return Result.success(tokenVO);
    }

    @Operation(summary = "用户注销")
    @Log(module = "认证管理", operation = "用户注销")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        authService.logout(token);
        return Result.success();
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<TokenVO> refreshToken(@RequestBody String refreshToken) {
        TokenVO tokenVO = authService.refreshToken(refreshToken);
        return Result.success(tokenVO);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserInfoVO userInfo = authService.getUserInfo(username);
        return Result.success(userInfo);
    }
}
