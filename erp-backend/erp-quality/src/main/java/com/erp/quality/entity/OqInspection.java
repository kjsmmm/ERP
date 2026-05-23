package com.erp.quality.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oq_inspection")
public class OqInspection extends BaseEntity {

    private String inspectionNo;
    private Long workOrderId;
    private Long productId;
    private Integer quantity;
    private Integer inspectionResult;
    private Integer status;
    private Long inspectorId;
    private LocalDateTime inspectionTime;
    private String remark;
}
