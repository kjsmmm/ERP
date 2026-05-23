package com.erp.product.dto;

import lombok.Data;

/**
 * 产品查询DTO
 */
@Data
public class ProductQueryDTO {

    /**
     * 产品名称/编码（模糊搜索）
     */
    private String keyword;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 产品类型
     */
    private Integer productType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;
}
