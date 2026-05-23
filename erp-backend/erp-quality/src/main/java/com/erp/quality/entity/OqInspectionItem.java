package com.erp.quality.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oq_inspection_item")
public class OqInspectionItem extends BaseEntity {

    private Long oqInspectionId;
    private String itemName;
    private String inspectionMethod;
    private String standardValue;
    private String actualValue;
    private Integer judgment;
}
