package com.erp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.system.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<SysRole> {

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    List<SysRole> getAllRoles();

    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 角色ID
     */
    Long createRole(SysRole role);

    /**
     * 更新角色
     *
     * @param id   角色ID
     * @param role 角色信息
     */
    void updateRole(Long id, SysRole role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 分配权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);
}
