package com.erp.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 产品类型（1原材料 2半成品 3成品）
     */
    private Integer productType;

    /**
     * 规格型号
     */
    private String spec;

    /**
     * 单位
     */
    private String unit;

    /**
     * 重量(kg)
     */
    private BigDecimal weight;

    /**
     * 标准成本
     */
    private BigDecimal standardCost;

    /**
     * 标准售价
     */
    private BigDecimal standardPrice;

    /**
     * 状态（0停用 1正常）
     */
    private Integer status;

    /**
     * 图片列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<ProductImage> images;

    /**
     * BOM子项列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<BomItem> bomItems;
}
