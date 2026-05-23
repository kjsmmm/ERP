package com.erp.customer.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户类型枚举
 */
@Getter
@AllArgsConstructor
public enum CustomerType {

    DOMESTIC(1, "国内"),
    FOREIGN(2, "国外");

    @EnumValue
    private final int code;

    @JsonValue
    private final String desc;

    public static CustomerType fromCode(int code) {
        for (CustomerType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的客户类型: " + code);
    }
}
