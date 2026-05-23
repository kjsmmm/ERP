package com.erp.quality.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DefectRecordDTO {
    @NotNull(message = "来源类型不能为空")
    private Integer sourceType;
    @NotNull(message = "来源ID不能为空")
    private Long sourceId;
    @NotNull(message = "产品不能为空")
    private Long productId;
    @NotNull(message = "数量不能为空")
    private Integer quantity;
    private String defectReason;
    @NotNull(message = "处理方式不能为空")
    private Integer handleType;
    private String handleRemark;
}
