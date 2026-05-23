package com.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.utils.PasswordUtils;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.dto.UserDTO;
import com.erp.system.dto.UserQueryDTO;
import com.erp.system.entity.SysUser;
import com.erp.system.entity.SysUserRole;
import com.erp.system.mapper.SysUserMapper;
import com.erp.system.mapper.SysUserRoleMapper;
import com.erp.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    private final SysUserRoleMapper userRoleMapper;

    @Override
    public IPage<SysUser> getUserPage(UserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getUsername()), SysUser::getUsername, queryDTO.getUsername());
        wrapper.like(StringUtils.hasText(queryDTO.getRealName()), SysUser::getRealName, queryDTO.getRealName());
        wrapper.eq(queryDTO.getStatus() != null, SysUser::getStatus, queryDTO.getStatus());
        wrapper.eq(queryDTO.getDeptId() != null, SysUser::getDeptId, queryDTO.getDeptId());
        wrapper.orderByDesc(SysUser::getCreatedAt);

        return page(page, wrapper);
    }

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        SysUser existUser = getByUsername(userDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS, "用户名已存在");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(userDTO.getUsername());
        user.setPassword(PasswordUtils.encode(userDTO.getPassword()));
        user.setNickname(userDTO.getNickname());
        user.setRealName(userDTO.getRealName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setGender(userDTO.getGender());
        user.setDeptId(userDTO.getDeptId());
        user.setStatus(1);
        user.setRemark(userDTO.getRemark());

        save(user);

        // 保存用户角色关联
        if (!CollectionUtils.isEmpty(userDTO.getRoleIds())) {
            for (Long roleId : userDTO.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }

        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserDTO userDTO) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户信息
        user.setNickname(userDTO.getNickname());
        user.setRealName(userDTO.getRealName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setGender(userDTO.getGender());
        user.setDeptId(userDTO.getDeptId());
        user.setRemark(userDTO.getRemark());

        updateById(user);

        // 更新用户角色关联
        if (!CollectionUtils.isEmpty(userDTO.getRoleIds())) {
            // 删除原有角色关联
            userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                    .eq(SysUserRole::getUserId, id));

            // 添加新的角色关联
            for (Long roleId : userDTO.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(id);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 逻辑删除用户
        removeById(id);

        // 删除用户角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));
    }

    @Override
    public String resetPassword(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 生成新密码
        String newPassword = PasswordUtils.generateRandomPassword(12);
        user.setPassword(PasswordUtils.encode(newPassword));
        updateById(user);

        return newPassword;
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(status);
        updateById(user);
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtils.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_ERROR, "原密码错误");
        }

        // 更新密码
        user.setPassword(PasswordUtils.encode(newPassword));
        updateById(user);
    }
}
