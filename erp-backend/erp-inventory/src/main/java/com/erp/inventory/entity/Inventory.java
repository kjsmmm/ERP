package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库存实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory")
public class Inventory extends BaseEntity {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 实物库存量
     */
    private BigDecimal onHandQty = BigDecimal.ZERO;

    /**
     * 预留量
     */
    private BigDecimal reservedQty = BigDecimal.ZERO;

    /**
     * 安全库存量
     */
    private BigDecimal safetyStock = BigDecimal.ZERO;

    /**
     * 可用量（非数据库字段，动态计算）
     */
    @TableField(exist = false)
    private BigDecimal availableQty;

    /**
     * 产品名称（非数据库字段）
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 产品编码（非数据库字段）
     */
    @TableField(exist = false)
    private String productCode;

    /**
     * 产品单位（非数据库字段）
     */
    @TableField(exist = false)
    private String unit;

    /**
     * 仓库名称（非数据库字段）
     */
    @TableField(exist = false)
    private String warehouseName;
}
