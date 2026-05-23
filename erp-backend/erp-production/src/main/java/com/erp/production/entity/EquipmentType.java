package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("equipment_type")
public class EquipmentType extends BaseEntity {
    private String typeCode;
    private String typeName;
    private String description;
}
