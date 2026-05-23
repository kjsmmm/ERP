package com.erp.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.finance.entity.ApRecord;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ApRecordService extends IService<ApRecord> {
    IPage<ApRecord> getApRecordPage(String keyword, Integer status, Long supplierId, Integer pageNum, Integer pageSize);

    /**
     * 由采购入库触发：自动创建应付单
     */
    void createFromReceipt(Long receiptId, Long purchaseOrderId, Long supplierId, BigDecimal amount);

    /**
     * 登记发票
     */
    void updateInvoice(Long id, String invoiceNo, LocalDate invoiceDate);

    /**
     * 付款核销
     */
    void addPayment(Long apId, String paymentMethod, BigDecimal amount, LocalDate paymentDate, String remark);
}
