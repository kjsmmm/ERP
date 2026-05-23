package com.erp.common.context;

/**
 * 用户ID提供者接口
 * 由具体模块实现，避免 erp-common 直接依赖 SecurityContextHolder
 */
public interface UserIdProvider {

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID，未登录时返回null
     */
    Long getCurrentUserId();
}
