package com.erp.system.context;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.context.UserIdProvider;
import com.erp.system.entity.SysUser;
import com.erp.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 从 SecurityContextHolder 获取当前用户ID
 */
@Slf4j
@Component
public class UserIdProviderImpl implements UserIdProvider {

    private final SysUserMapper userMapper;

    @Autowired
    public UserIdProviderImpl(@Lazy SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                if (username != null && !username.equals("anonymousUser")) {
                    SysUser user = userMapper.selectOne(
                            new LambdaQueryWrapper<SysUser>()
                                    .select(SysUser::getId)
                                    .eq(SysUser::getUsername, username)
                                    .eq(SysUser::getDeleted, 0));
                    return user != null ? user.getId() : null;
                }
            }
        } catch (Exception e) {
            log.debug("获取当前用户ID失败: {}", e.getMessage());
        }
        return null;
    }
}
