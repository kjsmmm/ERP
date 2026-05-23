package com.erp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.system.entity.SysPermission;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService extends IService<SysPermission> {

    /**
     * 获取权限树
     *
     * @return 权限树
     */
    List<SysPermission> getPermissionTree();

    /**
     * 创建权限
     *
     * @param permission 权限信息
     * @return 权限ID
     */
    Long createPermission(SysPermission permission);

    /**
     * 更新权限
     *
     * @param id         权限ID
     * @param permission 权限信息
     */
    void updatePermission(Long id, SysPermission permission);

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    void deletePermission(Long id);

    /**
     * 根据角色ID获取权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);
}
