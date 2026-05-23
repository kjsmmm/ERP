package com.erp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户 Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询用户的角色编码和权限编码
     * 返回格式：角色编码带 ROLE_ 前缀，权限编码原样返回
     */
    @Select({
        "SELECT CONCAT('ROLE_', r.role_code) AS authority FROM sys_user_role ur",
        "INNER JOIN sys_role r ON ur.role_id = r.id AND r.deleted = 0 AND r.status = 1",
        "WHERE ur.user_id = #{userId}",
        "UNION",
        "SELECT p.perm_code AS authority FROM sys_user_role ur",
        "INNER JOIN sys_role_permission rp ON ur.role_id = rp.role_id",
        "INNER JOIN sys_permission p ON rp.permission_id = p.id AND p.deleted = 0 AND p.status = 1",
        "WHERE ur.user_id = #{userId}"
    })
    List<String> selectUserAuthorities(Long userId);
}
