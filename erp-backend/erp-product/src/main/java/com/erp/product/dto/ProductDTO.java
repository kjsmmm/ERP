package com.erp.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品新增/编辑DTO
 */
@Data
public class ProductDTO {

    /**
     * 产品编码
     */
    @NotBlank(message = "产品编码不能为空")
    private String productCode;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    private String productName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 产品类型（1原材料 2半成品 3成品）
     */
    @NotNull(message = "产品类型不能为空")
    private Integer productType;

    /**
     * 规格型号
     */
    private String spec;

    /**
     * 单位
     */
    @NotBlank(message = "单位不能为空")
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
     * 备注
     */
    private String remark;
}
