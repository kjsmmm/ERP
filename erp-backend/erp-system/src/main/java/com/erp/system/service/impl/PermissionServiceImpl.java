package com.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysPermission;
import com.erp.system.entity.SysRolePermission;
import com.erp.system.mapper.SysPermissionMapper;
import com.erp.system.mapper.SysRolePermissionMapper;
import com.erp.system.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements PermissionService {

    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public List<SysPermission> getPermissionTree() {
        List<SysPermission> allPermissions = list(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getStatus, 1)
                .orderByAsc(SysPermission::getSortOrder));

        return buildTree(allPermissions, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(SysPermission permission) {
        // 检查权限编码是否已存在
        if (permission.getPermCode() != null) {
            SysPermission existPerm = getOne(new LambdaQueryWrapper<SysPermission>()
                    .eq(SysPermission::getPermCode, permission.getPermCode()));
            if (existPerm != null) {
                throw new BusinessException(ErrorCode.PERMISSION_CODE_EXISTS, "权限编码已存在");
            }
        }

        permission.setStatus(1);
        save(permission);
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(Long id, SysPermission permission) {
        SysPermission existPerm = getById(id);
        if (existPerm == null) {
            throw new BusinessException("权限不存在");
        }

        // 检查权限编码是否已存在（排除自身）
        if (permission.getPermCode() != null) {
            SysPermission codePerm = getOne(new LambdaQueryWrapper<SysPermission>()
                    .eq(SysPermission::getPermCode, permission.getPermCode())
                    .ne(SysPermission::getId, id));
            if (codePerm != null) {
                throw new BusinessException(ErrorCode.PERMISSION_CODE_EXISTS, "权限编码已存在");
            }
        }

        permission.setId(id);
        updateById(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        SysPermission permission = getById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }

        // 检查是否有子权限
        Long childCount = count(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.PERMISSION_HAS_CHILDREN, "存在子权限，无法删除");
        }

        // 删除权限
        removeById(id);

        // 删除角色权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getPermissionId, id));
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        List<SysRolePermission> rolePermissions = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId));
        return rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    /**
     * 递归构建权限树
     */
    private List<SysPermission> buildTree(List<SysPermission> allPermissions, Long parentId) {
        Map<Long, List<SysPermission>> parentMap = allPermissions.stream()
                .collect(Collectors.groupingBy(SysPermission::getParentId));

        return buildTreeRecursive(parentMap, parentId);
    }

    private List<SysPermission> buildTreeRecursive(Map<Long, List<SysPermission>> parentMap, Long parentId) {
        List<SysPermission> children = parentMap.getOrDefault(parentId, new ArrayList<>());
        for (SysPermission child : children) {
            child.setChildren(buildTreeRecursive(parentMap, child.getId()));
        }
        return children;
    }
}
