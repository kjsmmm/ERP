package com.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ar_payment")
public class ArPayment extends BaseEntity {

    private String paymentNo;
    private Long arId;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDate paymentDate;
    private String remark;
}
