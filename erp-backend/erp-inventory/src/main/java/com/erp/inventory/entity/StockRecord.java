package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库存流水实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stock_record")
public class StockRecord extends BaseEntity {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 操作类型（INBOUND/OUTBOUND/RESERVE/RELEASE）
     */
    private String recordType;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 关联单据号
     */
    private String referenceNo;

    /**
     * 关联单据类型（ORDER/PURCHASE/PRODUCTION）
     */
    private String referenceType;

    /**
     * 关联单据ID
     */
    private Long referenceId;
}
