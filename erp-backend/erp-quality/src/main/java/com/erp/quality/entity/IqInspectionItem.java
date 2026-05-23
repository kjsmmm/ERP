package com.erp.quality.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("iq_inspection_item")
public class IqInspectionItem extends BaseEntity {

    private Long iqInspectionId;
    private String itemName;
    private String inspectionMethod;
    private String standardValue;
    private String actualValue;
    private Integer judgment;
}
