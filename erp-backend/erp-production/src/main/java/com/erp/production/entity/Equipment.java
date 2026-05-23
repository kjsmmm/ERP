package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("equipment")
public class Equipment extends BaseEntity {
    private String equipmentCode;
    private String equipmentName;
    private Long equipmentTypeId;
    private Long workshopId;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastMaintenanceDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextMaintenanceDate;

    @TableField(exist = false)
    private String equipmentTypeName;

    @TableField(exist = false)
    private String workshopName;
}
