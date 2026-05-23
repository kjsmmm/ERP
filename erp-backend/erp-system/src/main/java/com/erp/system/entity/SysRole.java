package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 数据范围（1全部 2本部门及下级 3本部门 4仅本人）
     */
    private Integer dataScope;

    /**
     * 状态（0停用 1正常）
     */
    private Integer status;

    /**
     * 权限列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysPermission> permissions;
}
