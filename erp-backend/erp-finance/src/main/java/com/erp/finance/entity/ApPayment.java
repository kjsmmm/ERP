package com.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ap_payment")
public class ApPayment extends BaseEntity {

    private String paymentNo;
    private Long apId;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDate paymentDate;
    private String remark;
}
