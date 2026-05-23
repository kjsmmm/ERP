package com.erp.order.dto;

import jakarta.validation.constraints.Max;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 订单查询DTO
 */
@Data
public class OrderQueryDTO {

    private Integer pageNum = 1;

    @Max(value = 100, message = "每页最多100条")
    private Integer pageSize = 10;

    /**
     * 关键字（订单号/客户名）
     */
    private String keyword;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
