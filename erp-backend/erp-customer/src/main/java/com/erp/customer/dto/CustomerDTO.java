package com.erp.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户新增/编辑DTO
 */
@Data
public class CustomerDTO {

    /**
     * 客户名称
     */
    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    /**
     * 客户类型（1国内 2国外）
     */
    @NotNull(message = "客户类型不能为空")
    private Integer customerType;

    /**
     * 行业
     */
    private String industry;

    /**
     * 客户等级（1A 2B 3C 4D）
     */
    private Integer customerLevel;

    /**
     * 客户来源
     */
    private String source;

    /**
     * 税号
     */
    private String taxNumber;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 账期
     */
    private String paymentTerms;

    /**
     * 信用额度
     */
    private BigDecimal creditLimit;

    /**
     * 地址
     */
    private String address;

    /**
     * 备注
     */
    private String remark;
}
