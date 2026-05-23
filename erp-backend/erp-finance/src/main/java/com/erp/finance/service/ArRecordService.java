package com.erp.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.finance.entity.ArRecord;

import java.math.BigDecimal;

public interface ArRecordService extends IService<ArRecord> {
    IPage<ArRecord> getArRecordPage(String keyword, Integer status, Long customerId, Integer pageNum, Integer pageSize);

    /**
     * 由发货出库触发：自动创建应收单
     */
    void createFromDelivery(Long deliveryId, Long orderId, Long customerId, BigDecimal amount);

    /**
     * 登记发票
     */
    void updateInvoice(Long id, String invoiceNo, java.time.LocalDate invoiceDate);

    /**
     * 收款核销
     */
    void addPayment(Long arId, String paymentMethod, BigDecimal amount, java.time.LocalDate paymentDate, String remark);
}
