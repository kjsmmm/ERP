package com.erp.production.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamDTO {
    @NotBlank(message = "班组编码不能为空")
    private String teamCode;
    @NotBlank(message = "班组名称不能为空")
    private String teamName;
    @NotNull(message = "所属车间不能为空")
    private Long workshopId;
    private Long leaderId;
    private Integer memberCount;
}
