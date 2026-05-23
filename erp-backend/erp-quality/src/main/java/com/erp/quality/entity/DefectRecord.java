package com.erp.quality.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("defect_record")
public class DefectRecord extends BaseEntity {

    private String recordNo;
    private Integer sourceType;
    private Long sourceId;
    private Long productId;
    private Integer quantity;
    private String defectReason;
    private Integer handleType;
    private String handleRemark;
    private Integer status;
    private String processInstanceId;
}
