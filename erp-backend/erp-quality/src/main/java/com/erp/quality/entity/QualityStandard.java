package com.erp.quality.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quality_standard")
public class QualityStandard extends BaseEntity {

    private String standardCode;
    private String standardName;
    private Integer applicableType;
    private Long categoryId;
    private Integer status;
}
