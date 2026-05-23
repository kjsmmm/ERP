package com.erp.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 联系人DTO
 */
@Data
public class ContactDTO {

    /**
     * 联系人ID（编辑时必填）
     */
    private Long id;

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    /**
     * 职位
     */
    private String position;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否主要联系人（0否 1是）
     */
    private Integer isPrimary;

    /**
     * 备注
     */
    private String remark;
}
