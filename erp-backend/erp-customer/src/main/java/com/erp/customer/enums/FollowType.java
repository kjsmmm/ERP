package com.erp.customer.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跟进类型枚举
 */
@Getter
@AllArgsConstructor
public enum FollowType {

    PHONE(1, "电话"),
    VISIT(2, "拜访"),
    EMAIL(3, "邮件"),
    WECHAT(4, "微信");

    @EnumValue
    private final int code;

    @JsonValue
    private final String desc;

    public static FollowType fromCode(int code) {
        for (FollowType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的跟进类型: " + code);
    }
}
