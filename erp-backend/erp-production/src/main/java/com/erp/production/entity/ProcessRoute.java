package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("process_route")
public class ProcessRoute extends BaseEntity {
    private Long productId;
    private String routeCode;
    private String routeName;
    private Integer version;
    private Integer isDefault;
    private Integer status;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private List<ProcessStep> steps;
}
