package com.erp.auth.service.impl;

import com.erp.auth.dto.LoginDTO;
import com.erp.auth.service.AuthService;
import com.erp.auth.utils.JwtUtils;
import com.erp.auth.vo.TokenVO;
import com.erp.auth.vo.UserInfoVO;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.utils.RedisUtils;
import com.erp.system.entity.SysUser;
import com.erp.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;

    private static final String LOGIN_FAIL_COUNT_KEY = "user:login:fail:";
    private static final String USER_TOKEN_KEY = "user:token:";
    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    private static final int LOCK_MINUTES = 30;

    @Override
    public TokenVO login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        // 1. 查询用户
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
                        .eq(SysUser::getDeleted, 0)
        );

        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAIL, "用户名或密码错误");
        }

        // 2. 检查账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED, "账号已被禁用");
        }

        // 3. 检查是否被锁定
        if (user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED, "账号已被锁定，请稍后再试");
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 增加登录失败次数
            int failCount = user.getLoginFailCount() == null ? 0 : user.getLoginFailCount();
            failCount++;

            // 更新失败次数
            SysUser updateuser = new SysUser();
            updateuser.setId(user.getId());
            updateuser.setLoginFailCount(failCount);

            // 检查是否需要锁定
            if (failCount >= MAX_LOGIN_FAIL_COUNT) {
                updateuser.setLockTime(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                updateuser.setLoginFailCount(0);
                userMapper.updateById(updateuser);
                throw new BusinessException(ErrorCode.ACCOUNT_LOCKED,
                        "登录失败次数过多，账号已锁定" + LOCK_MINUTES + "分钟");
            }

            userMapper.updateById(updateuser);
            throw new BusinessException(ErrorCode.LOGIN_FAIL, "用户名或密码错误");
        }

        // 5. 登录成功，重置失败次数
        SysUser updateUser = new SysUser();
        updateUser.setId(user.getId());
        updateUser.setLoginFailCount(0);
        updateUser.setLockTime(null);
        updateUser.setLoginDate(LocalDateTime.now());
        userMapper.updateById(updateUser);

        // 6. 生成 Token
        String accessToken = jwtUtils.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getUsername());

        // 7. 存储 Token 到 Redis
        redisUtils.set(USER_TOKEN_KEY + user.getUsername(), accessToken,
                jwtUtils.getAccessTokenExpireSeconds(), TimeUnit.SECONDS);

        log.info("用户登录成功: {}", username);

        // 8. 返回 Token 信息
        return TokenVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getAccessTokenExpireSeconds())
                .build();
    }

    @Override
    public void logout(String token) {
        try {
            String username = jwtUtils.getUsernameFromToken(token);
            redisUtils.delete(USER_TOKEN_KEY + username);
            log.info("用户注销成功: {}", username);
        } catch (Exception e) {
            log.warn("注销失败: {}", e.getMessage());
        }
    }

    @Override
    public TokenVO refreshToken(String refreshToken) {
        // 1. 验证 Refresh Token
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Refresh Token 无效或已过期");
        }

        // 2. 检查 Token 类型
        String tokenType = jwtUtils.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Token 类型错误");
        }

        // 3. 获取用户信息
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        Long userId = jwtUtils.getUserIdFromToken(refreshToken);

        // 4. 生成新的 Token
        String newAccessToken = jwtUtils.generateAccessToken(userId, username);
        String newRefreshToken = jwtUtils.generateRefreshToken(userId, username);

        // 5. 更新 Redis 中的 Token
        redisUtils.set(USER_TOKEN_KEY + username, newAccessToken,
                jwtUtils.getAccessTokenExpireSeconds(), TimeUnit.SECONDS);

        log.info("Token 刷新成功: {}", username);

        // 6. 返回新的 Token 信息
        return TokenVO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getAccessTokenExpireSeconds())
                .build();
    }

    @Override
    public UserInfoVO getUserInfo(String username) {
        // 1. 查询用户
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
                        .eq(SysUser::getDeleted, 0)
        );

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 2. 查询权限
        java.util.List<String> authorities = userMapper.selectUserAuthorities(user.getId());

        // 3. 分离角色和权限
        java.util.List<String> roles = new java.util.ArrayList<>();
        java.util.List<String> permissions = new java.util.ArrayList<>();
        for (String auth : authorities) {
            if (auth.startsWith("ROLE_")) {
                roles.add(auth.substring(5));
            } else {
                permissions.add(auth);
            }
        }

        return UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}
