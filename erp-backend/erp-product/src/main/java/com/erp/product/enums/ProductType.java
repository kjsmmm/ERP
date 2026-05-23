package com.erp.product.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 产品类型枚举
 */
@Getter
@AllArgsConstructor
public enum ProductType {

    RAW_MATERIAL(1, "原材料"),
    SEMI_FINISHED(2, "半成品"),
    FINISHED(3, "成品");

    @EnumValue
    private final int code;

    @JsonValue
    private final String desc;

    public static ProductType fromCode(int code) {
        for (ProductType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的产品类型: " + code);
    }
}
