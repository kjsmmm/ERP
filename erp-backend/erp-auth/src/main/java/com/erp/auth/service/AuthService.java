package com.erp.auth.service;

import com.erp.auth.dto.LoginDTO;
import com.erp.auth.vo.TokenVO;
import com.erp.auth.vo.UserInfoVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求
     * @return Token 信息
     */
    TokenVO login(LoginDTO loginDTO);

    /**
     * 用户注销
     *
     * @param token Access Token
     */
    void logout(String token);

    /**
     * 刷新 Token
     *
     * @param refreshToken Refresh Token
     * @return 新的 Token 信息
     */
    TokenVO refreshToken(String refreshToken);

    /**
     * 获取当前登录用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserInfoVO getUserInfo(String username);
}
