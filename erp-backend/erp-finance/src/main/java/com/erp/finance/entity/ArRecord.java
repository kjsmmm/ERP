package com.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ar_record")
public class ArRecord extends BaseEntity {

    private String receivableNo;
    private Long deliveryId;
    private Long orderId;
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private String invoiceNo;
    private LocalDate invoiceDate;
    private Integer invoiceStatus;
    private Integer status;
    private String remark;

    @TableField(exist = false)
    private String customerName;

    @TableField(exist = false)
    private List<ArPayment> payments;
}
