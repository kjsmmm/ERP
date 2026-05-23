package com.erp.purchase.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierDTO {
    @NotBlank(message = "供应商编码不能为空")
    private String supplierCode;
    @NotBlank(message = "供应商名称不能为空")
    private String supplierName;
    private String contactName;
    private String phone;
    private String address;
}
