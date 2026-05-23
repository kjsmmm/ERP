package com.erp.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.product.dto.CategoryDTO;
import com.erp.product.entity.ProductCategory;

import java.util.List;

/**
 * 产品分类服务接口
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 获取分类树
     *
     * @return 分类树列表
     */
    List<ProductCategory> getCategoryTree();

    /**
     * 创建分类
     *
     * @param dto 分类信息
     * @return 分类ID
     */
    Long createCategory(CategoryDTO dto);

    /**
     * 更新分类
     *
     * @param id  分类ID
     * @param dto 分类信息
     */
    void updateCategory(Long id, CategoryDTO dto);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);
}
