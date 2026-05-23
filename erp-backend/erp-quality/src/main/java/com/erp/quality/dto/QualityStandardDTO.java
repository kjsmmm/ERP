package com.erp.quality.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QualityStandardDTO {
    @NotBlank(message = "标准编码不能为空")
    private String standardCode;
    @NotBlank(message = "标准名称不能为空")
    private String standardName;
    @NotNull(message = "适用类型不能为空")
    private Integer applicableType;
    private Long categoryId;
    @NotEmpty(message = "检验项目不能为空")
    @Valid
    private List<QualityStandardItemDTO> items;

    @Data
    public static class QualityStandardItemDTO {
        @NotBlank(message = "项目名称不能为空")
        private String itemName;
        private String inspectionMethod;
        private String standardValue;
        private String judgmentRule;
    }
}
