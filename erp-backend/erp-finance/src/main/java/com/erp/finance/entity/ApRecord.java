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
@TableName("ap_record")
public class ApRecord extends BaseEntity {

    private String payableNo;
    private Long receiptId;
    private Long purchaseOrderId;
    private Long supplierId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private String invoiceNo;
    private LocalDate invoiceDate;
    private Integer invoiceStatus;
    private Integer status;
    private String remark;

    @TableField(exist = false)
    private String supplierName;

    @TableField(exist = false)
    private List<ApPayment> payments;
}
