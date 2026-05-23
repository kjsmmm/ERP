package com.erp.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户联系人实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_contact")
public class CustomerContact extends BaseEntity {

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 职位
     */
    private String position;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否主要联系人（0否 1是）
     */
    private Integer isPrimary;
}
