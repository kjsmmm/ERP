package com.erp.customer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 客户跟进记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_follow")
public class CustomerFollow extends BaseEntity {

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 联系人ID
     */
    private Long contactId;

    /**
     * 跟进类型（1电话 2拜访 3邮件 4微信）
     */
    private Integer followType;

    /**
     * 跟进内容
     */
    private String content;

    /**
     * 跟进时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime followTime;

    /**
     * 下次跟进时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowTime;

    /**
     * 跟进人ID
     */
    private Long operatorId;

    /**
     * 联系人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String contactName;

    /**
     * 操作人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String operatorName;
}
