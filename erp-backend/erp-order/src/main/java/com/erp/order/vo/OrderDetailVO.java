package com.erp.order.vo;

import com.erp.order.entity.OrderItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情VO
 */
@Data
public class OrderDetailVO {

    private Long id;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private String customerCode;
    private Integer status;
    private BigDecimal totalAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    private String deliveryAddress;
    private String contactPhone;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private List<OrderItem> items;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 审批状态（pending/approved/rejected/null）
     */
    private String approvalStatus;

    /**
     * 发货状态：0=未发货 1=部分发货 2=全部发货
     */
    private Integer deliveryStatus;

    /**
     * 已发货总数
     */
    private Integer totalDeliveredQty;
}
