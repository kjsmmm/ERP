package com.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.finance.entity.ArPayment;
import com.erp.finance.entity.ArRecord;
import com.erp.finance.mapper.ArPaymentMapper;
import com.erp.finance.mapper.ArRecordMapper;
import com.erp.finance.service.ArRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ArRecordServiceImpl extends ServiceImpl<ArRecordMapper, ArRecord> implements ArRecordService {

    private final ArPaymentMapper arPaymentMapper;

    @Override
    public IPage<ArRecord> getArRecordPage(String keyword, Integer status, Long customerId, Integer pageNum, Integer pageSize) {
        Page<ArRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ArRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ArRecord::getReceivableNo, keyword);
        }
        if (status != null) {
            wrapper.eq(ArRecord::getStatus, status);
        }
        if (customerId != null) {
            wrapper.eq(ArRecord::getCustomerId, customerId);
        }
        wrapper.orderByDesc(ArRecord::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void createFromDelivery(Long deliveryId, Long orderId, Long customerId, BigDecimal amount) {
        ArRecord record = new ArRecord();
        record.setReceivableNo(generateReceivableNo());
        record.setDeliveryId(deliveryId);
        record.setOrderId(orderId);
        record.setCustomerId(customerId);
        record.setAmount(amount);
        record.setPaidAmount(BigDecimal.ZERO);
        record.setInvoiceStatus(0);
        record.setStatus(0);
        save(record);
    }

    @Override
    @Transactional
    public void updateInvoice(Long id, String invoiceNo, LocalDate invoiceDate) {
        ArRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.AR_NOT_FOUND, "应收单不存在");
        }
        record.setInvoiceNo(invoiceNo);
        record.setInvoiceDate(invoiceDate);
        record.setInvoiceStatus(1);
        updateById(record);
    }

    @Override
    @Transactional
    public void addPayment(Long arId, String paymentMethod, BigDecimal amount, LocalDate paymentDate, String remark) {
        ArRecord record = getById(arId);
        if (record == null) {
            throw new BusinessException(ErrorCode.AR_NOT_FOUND, "应收单不存在");
        }
        BigDecimal newPaid = record.getPaidAmount().add(amount);
        if (newPaid.compareTo(record.getAmount()) > 0) {
            throw new BusinessException(ErrorCode.AR_PAYMENT_EXCEEDED, "收款金额超出应收金额");
        }

        ArPayment payment = new ArPayment();
        payment.setPaymentNo(generatePaymentNo("RCV"));
        payment.setArId(arId);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(amount);
        payment.setPaymentDate(paymentDate);
        payment.setRemark(remark);
        arPaymentMapper.insert(payment);

        record.setPaidAmount(newPaid);
        if (newPaid.compareTo(record.getAmount()) >= 0) {
            record.setStatus(2);
        } else {
            record.setStatus(1);
        }
        updateById(record);
    }

    private String generateReceivableNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "AR-" + dateStr + "-";
        ArRecord max = getOne(new LambdaQueryWrapper<ArRecord>()
                .likeRight(ArRecord::getReceivableNo, prefix)
                .orderByDesc(ArRecord::getReceivableNo)
                .last("LIMIT 1"));
        int seq = 1;
        if (max != null && max.getReceivableNo() != null && max.getReceivableNo().startsWith(prefix)) {
            try {
                seq = Integer.parseInt(max.getReceivableNo().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("AR-%s-%03d", dateStr, seq);
    }

    private String generatePaymentNo(String prefix) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return prefix + "-" + dateStr + "-" + (arPaymentMapper.selectCount(null) + 1);
    }
}
