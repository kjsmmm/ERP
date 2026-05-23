package com.erp.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 产品分类新增/编辑DTO
 */
@Data
public class CategoryDTO {

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 父分类ID（0=顶级）
     */
    private Long parentId;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}
