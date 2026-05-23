package com.erp.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.product.entity.ProductImage;
import com.erp.product.mapper.ProductImageMapper;
import com.erp.product.service.ProductImageService;
import com.erp.common.utils.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 产品图片服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl extends ServiceImpl<ProductImageMapper, ProductImage> implements ProductImageService {

    private final FileUploadUtils fileUploadUtils;

    @Override
    public List<ProductImage> getImagesByProductId(Long productId) {
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId, productId);
        wrapper.orderByDesc(ProductImage::getIsPrimary).orderByAsc(ProductImage::getSortOrder);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadImage(Long productId, MultipartFile file) {
        // 上传文件
        String imageUrl = fileUploadUtils.upload(file);

        ProductImage image = new ProductImage();
        image.setProductId(productId);
        image.setImageUrl(imageUrl);
        image.setSortOrder(0);

        // 如果是第一张图，自动设为主图
        long count = count(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId));
        image.setIsPrimary(count == 0 ? 1 : 0);

        save(image);
        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPrimary(Long imageId) {
        ProductImage image = getById(imageId);
        if (image == null) {
            throw new BusinessException("图片不存在");
        }

        // 清除该产品所有图片的主图标记
        ProductImage update = new ProductImage();
        update.setIsPrimary(0);
        update(update, new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, image.getProductId()));

        // 设置当前图片为主图
        image.setIsPrimary(1);
        updateById(image);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(Long imageId) {
        ProductImage image = getById(imageId);
        if (image == null) {
            throw new BusinessException("图片不存在");
        }

        // 删除文件
        fileUploadUtils.delete(image.getImageUrl());

        // 删除记录
        removeById(imageId);

        // 如果删除的是主图，自动提升下一张为主图
        if (Integer.valueOf(1).equals(image.getIsPrimary())) {
            List<ProductImage> remaining = getImagesByProductId(image.getProductId());
            if (!remaining.isEmpty()) {
                ProductImage next = remaining.get(0);
                next.setIsPrimary(1);
                updateById(next);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByProductId(Long productId) {
        List<ProductImage> images = getImagesByProductId(productId);
        for (ProductImage image : images) {
            fileUploadUtils.delete(image.getImageUrl());
        }
        remove(new LambdaQueryWrapper<ProductImage>().eq(ProductImage::getProductId, productId));
    }
}
