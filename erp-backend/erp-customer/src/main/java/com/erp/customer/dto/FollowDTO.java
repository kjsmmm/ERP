package com.erp.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 跟进记录DTO
 */
@Data
public class FollowDTO {

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 联系人ID（可选）
     */
    private Long contactId;

    /**
     * 跟进类型（1电话 2拜访 3邮件 4微信）
     */
    @NotNull(message = "跟进类型不能为空")
    private Integer followType;

    /**
     * 跟进内容
     */
    @NotBlank(message = "跟进内容不能为空")
    private String content;

    /**
     * 跟进时间
     */
    @NotNull(message = "跟进时间不能为空")
    private LocalDateTime followTime;

    /**
     * 下次跟进时间
     */
    private LocalDateTime nextFollowTime;

    /**
     * 备注
     */
    private String remark;
}
