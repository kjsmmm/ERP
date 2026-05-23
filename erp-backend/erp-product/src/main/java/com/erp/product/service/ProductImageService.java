package com.erp.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.product.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 产品图片服务接口
 */
public interface ProductImageService extends IService<ProductImage> {

    /**
     * 获取产品图片列表
     *
     * @param productId 产品ID
     * @return 图片列表
     */
    List<ProductImage> getImagesByProductId(Long productId);

    /**
     * 上传产品图片
     *
     * @param productId 产品ID
     * @param file      图片文件
     * @return 图片ID
     */
    Long uploadImage(Long productId, MultipartFile file);

    /**
     * 设为主图
     *
     * @param imageId 图片ID
     */
    void setPrimary(Long imageId);

    /**
     * 删除图片
     *
     * @param imageId 图片ID
     */
    void deleteImage(Long imageId);

    /**
     * 删除产品下所有图片（级联删除）
     *
     * @param productId 产品ID
     */
    void deleteByProductId(Long productId);
}
