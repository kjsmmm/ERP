package com.erp.customer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer")
public class Customer extends BaseEntity {

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户类型（1国内 2国外）
     */
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
     * 状态（0停用 1启用）
     */
    private Integer status;

    /**
     * 联系人列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<CustomerContact> contacts;
}
