package com.erp.quality.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("iq_inspection")
public class IqInspection extends BaseEntity {

    private String inspectionNo;
    private Long purchaseOrderId;
    private Long supplierId;
    private Integer inspectionResult;
    private Integer status;
    private Long inspectorId;
    private LocalDateTime inspectionTime;
    private String remark;
}
