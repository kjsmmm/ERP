package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("team")
public class Team extends BaseEntity {
    private String teamCode;
    private String teamName;
    private Long workshopId;
    private Long leaderId;
    private Integer memberCount;
    private Integer status;

    @TableField(exist = false)
    private String workshopName;

    @TableField(exist = false)
    private String leaderName;
}
