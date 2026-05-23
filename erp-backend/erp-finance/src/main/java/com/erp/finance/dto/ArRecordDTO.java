package com.erp.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ArRecordDTO {

    @NotNull(message = "发票信息不能为空")
    private String invoiceNo;
    private LocalDate invoiceDate;

    @Data
    public static class ArPaymentDTO {
        @NotNull(message = "收款金额不能为空")
        @NotBlank(message = "收款金额不能为空")
        private String amount;

        private String paymentMethod;
        private LocalDate paymentDate;
        private String remark;
    }
}
