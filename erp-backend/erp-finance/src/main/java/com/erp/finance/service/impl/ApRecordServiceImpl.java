package com.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.finance.entity.ApPayment;
import com.erp.finance.entity.ApRecord;
import com.erp.finance.mapper.ApPaymentMapper;
import com.erp.finance.mapper.ApRecordMapper;
import com.erp.finance.service.ApRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ApRecordServiceImpl extends ServiceImpl<ApRecordMapper, ApRecord> implements ApRecordService {

    private final ApPaymentMapper apPaymentMapper;

    @Override
    public IPage<ApRecord> getApRecordPage(String keyword, Integer status, Long supplierId, Integer pageNum, Integer pageSize) {
        Page<ApRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ApRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ApRecord::getPayableNo, keyword);
        }
        if (status != null) {
            wrapper.eq(ApRecord::getStatus, status);
        }
        if (supplierId != null) {
            wrapper.eq(ApRecord::getSupplierId, supplierId);
        }
        wrapper.orderByDesc(ApRecord::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void createFromReceipt(Long receiptId, Long purchaseOrderId, Long supplierId, BigDecimal amount) {
        ApRecord record = new ApRecord();
        record.setPayableNo(generatePayableNo());
        record.setReceiptId(receiptId);
        record.setPurchaseOrderId(purchaseOrderId);
        record.setSupplierId(supplierId);
        record.setAmount(amount);
        record.setPaidAmount(BigDecimal.ZERO);
        record.setInvoiceStatus(0);
        record.setStatus(0);
        save(record);
    }

    @Override
    @Transactional
    public void updateInvoice(Long id, String invoiceNo, LocalDate invoiceDate) {
        ApRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.AP_NOT_FOUND, "应付单不存在");
        }
        record.setInvoiceNo(invoiceNo);
        record.setInvoiceDate(invoiceDate);
        record.setInvoiceStatus(1);
        updateById(record);
    }

    @Override
    @Transactional
    public void addPayment(Long apId, String paymentMethod, BigDecimal amount, LocalDate paymentDate, String remark) {
        ApRecord record = getById(apId);
        if (record == null) {
            throw new BusinessException(ErrorCode.AP_NOT_FOUND, "应付单不存在");
        }
        BigDecimal newPaid = record.getPaidAmount().add(amount);
        if (newPaid.compareTo(record.getAmount()) > 0) {
            throw new BusinessException(ErrorCode.AP_PAYMENT_EXCEEDED, "付款金额超出应付金额");
        }

        ApPayment payment = new ApPayment();
        payment.setPaymentNo(generatePaymentNo("PMT"));
        payment.setApId(apId);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(amount);
        payment.setPaymentDate(paymentDate);
        payment.setRemark(remark);
        apPaymentMapper.insert(payment);

        record.setPaidAmount(newPaid);
        if (newPaid.compareTo(record.getAmount()) >= 0) {
            record.setStatus(2);
        } else {
            record.setStatus(1);
        }
        updateById(record);
    }

    private String generatePayableNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "AP-" + dateStr + "-";
        ApRecord max = getOne(new LambdaQueryWrapper<ApRecord>()
                .likeRight(ApRecord::getPayableNo, prefix)
                .orderByDesc(ApRecord::getPayableNo)
                .last("LIMIT 1"));
        int seq = 1;
        if (max != null && max.getPayableNo() != null && max.getPayableNo().startsWith(prefix)) {
            try {
                seq = Integer.parseInt(max.getPayableNo().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("AP-%s-%03d", dateStr, seq);
    }

    private String generatePaymentNo(String prefix) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return prefix + "-" + dateStr + "-" + (apPaymentMapper.selectCount(null) + 1);
    }
}
