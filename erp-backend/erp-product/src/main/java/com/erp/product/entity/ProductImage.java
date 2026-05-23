package com.erp.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品图片实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_image")
public class ProductImage extends BaseEntity {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 图片路径
     */
    private String imageUrl;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否主图（0否 1是）
     */
    private Integer isPrimary;
}
