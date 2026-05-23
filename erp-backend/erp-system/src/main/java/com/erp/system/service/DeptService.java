package com.erp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.system.entity.SysDept;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DeptService extends IService<SysDept> {

    /**
     * 获取部门树
     *
     * @return 部门树
     */
    List<SysDept> getDeptTree();

    /**
     * 创建部门
     *
     * @param dept 部门信息
     * @return 部门ID
     */
    Long createDept(SysDept dept);

    /**
     * 更新部门
     *
     * @param id   部门ID
     * @param dept 部门信息
     */
    void updateDept(Long id, SysDept dept);

    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    void deleteDept(Long id);
}
