package com.erp.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * BOM物料清单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bom_item")
public class BomItem extends BaseEntity {

    /**
     * 父产品ID
     */
    private Long productId;

    /**
     * 子物料ID（指向product表）
     */
    private Long materialId;

    /**
     * 用量
     */
    private BigDecimal quantity;

    /**
     * 损耗率(%)
     */
    private BigDecimal wasteRate;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 物料产品信息（非数据库字段，查询时填充）
     */
    @TableField(exist = false)
    private String materialName;

    @TableField(exist = false)
    private String materialCode;

    @TableField(exist = false)
    private String materialUnit;

    @TableField(exist = false)
    private String materialSpec;

    @TableField(exist = false)
    private Integer materialType;
}
