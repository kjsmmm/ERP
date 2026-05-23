package com.erp.customer.dto;

import lombok.Data;

/**
 * 客户查询DTO
 */
@Data
public class CustomerQueryDTO {

    /**
     * 客户名称/编码（模糊搜索）
     */
    private String keyword;

    /**
     * 客户类型
     */
    private Integer customerType;

    /**
     * 客户等级
     */
    private Integer customerLevel;

    /**
     * 行业
     */
    private String industry;

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
