package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("workshop")
public class Workshop extends BaseEntity {
    private String workshopCode;
    private String workshopName;
    private String address;
    private String manager;
    private String phone;
    private String description;
    private Integer status;
}
