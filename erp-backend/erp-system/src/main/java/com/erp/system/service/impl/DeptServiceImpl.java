package com.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysDept;
import com.erp.system.entity.SysUser;
import com.erp.system.mapper.SysDeptMapper;
import com.erp.system.mapper.SysUserMapper;
import com.erp.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements DeptService {

    private final SysUserMapper userMapper;

    @Override
    public List<SysDept> getDeptTree() {
        // 获取所有部门
        List<SysDept> allDepts = list(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getStatus, 1)
                .orderByAsc(SysDept::getSortOrder));

        // 构建部门树
        return buildDeptTree(allDepts, 0L);
    }

    /**
     * 递归构建部门树
     */
    private List<SysDept> buildDeptTree(List<SysDept> allDepts, Long parentId) {
        return allDepts.stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .peek(dept -> dept.setChildren(buildDeptTree(allDepts, dept.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDept(SysDept dept) {
        // 检查父部门是否存在
        if (dept.getParentId() != null && dept.getParentId() != 0) {
            SysDept parentDept = getById(dept.getParentId());
            if (parentDept == null) {
                throw new BusinessException("父部门不存在");
            }
        }

        dept.setStatus(1);
        save(dept);

        return dept.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(Long id, SysDept dept) {
        SysDept existDept = getById(id);
        if (existDept == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查是否将部门设置为自己的子部门
        if (dept.getParentId() != null && dept.getParentId().equals(id)) {
            throw new BusinessException("不能将部门设置为自己的子部门");
        }

        dept.setId(id);
        updateById(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long id) {
        SysDept dept = getById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查是否存在子部门
        Long childCount = count(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.DEPT_HAS_CHILDREN, "部门下存在子部门，无法删除");
        }

        // 检查部门下是否存在用户
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeptId, id));
        if (userCount > 0) {
            throw new BusinessException(ErrorCode.DEPT_HAS_USERS, "部门下存在用户，无法删除");
        }

        // 删除部门
        removeById(id);
    }
}
