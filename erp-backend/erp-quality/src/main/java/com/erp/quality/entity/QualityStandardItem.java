package com.erp.quality.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quality_standard_item")
public class QualityStandardItem extends BaseEntity {

    private Long standardId;
    private String itemName;
    private String inspectionMethod;
    private String standardValue;
    private String judgmentRule;
}
