package com.erp.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.product.dto.ProductDTO;
import com.erp.product.dto.ProductQueryDTO;
import com.erp.product.entity.Product;
import com.erp.product.vo.ProductDetailVO;

/**
 * 产品服务接口
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询产品
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<Product> getProductPage(ProductQueryDTO queryDTO);

    /**
     * 获取产品详情（含图片+BOM）
     *
     * @param id 产品ID
     * @return 产品详情
     */
    ProductDetailVO getProductDetail(Long id);

    /**
     * 创建产品
     *
     * @param dto 产品信息
     * @return 产品ID
     */
    Long createProduct(ProductDTO dto);

    /**
     * 更新产品
     *
     * @param id  产品ID
     * @param dto 产品信息
     */
    void updateProduct(Long id, ProductDTO dto);

    /**
     * 删除产品
     *
     * @param id 产品ID
     */
    void deleteProduct(Long id);

    /**
     * 修改产品状态
     *
     * @param id     产品ID
     * @param status 状态
     */
    void changeStatus(Long id, Integer status);
}
