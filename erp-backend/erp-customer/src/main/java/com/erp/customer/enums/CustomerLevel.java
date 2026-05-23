package com.erp.customer.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户等级枚举
 */
@Getter
@AllArgsConstructor
public enum CustomerLevel {

    A(1, "A"),
    B(2, "B"),
    C(3, "C"),
    D(4, "D");

    @EnumValue
    private final int code;

    @JsonValue
    private final String desc;

    public static CustomerLevel fromCode(int code) {
        for (CustomerLevel level : values()) {
            if (level.code == code) {
                return level;
            }
        }
        throw new IllegalArgumentException("未知的客户等级: " + code);
    }
}
