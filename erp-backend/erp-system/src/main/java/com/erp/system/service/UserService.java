package com.erp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.system.entity.SysUser;
import com.erp.system.dto.UserDTO;
import com.erp.system.dto.UserQueryDTO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<SysUser> {

    /**
     * 分页查询用户
     *
     * @param queryDTO 查询条件
     * @return 用户分页
     */
    IPage<SysUser> getUserPage(UserQueryDTO queryDTO);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);

    /**
     * 创建用户
     *
     * @param userDTO 用户信息
     * @return 用户ID
     */
    Long createUser(UserDTO userDTO);

    /**
     * 更新用户
     *
     * @param id     用户ID
     * @param userDTO 用户信息
     */
    void updateUser(Long id, UserDTO userDTO);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return 新密码
     */
    String resetPassword(Long id);

    /**
     * 修改用户状态
     *
     * @param id     用户ID
     * @param status 状态
     */
    void changeStatus(Long id, Integer status);

    /**
     * 修改密码
     *
     * @param id          用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long id, String oldPassword, String newPassword);
}
