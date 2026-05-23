package com.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.purchase.dto.PurchaseOrderDTO;
import com.erp.purchase.entity.PurchaseOrder;
import com.erp.purchase.entity.PurchaseOrderItem;
import com.erp.purchase.mapper.PurchaseOrderItemMapper;
import com.erp.purchase.mapper.PurchaseOrderMapper;
import com.erp.purchase.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    private final PurchaseOrderItemMapper purchaseOrderItemMapper;

    @Override
    public IPage<PurchaseOrder> getPurchaseOrderPage(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<PurchaseOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PurchaseOrder::getOrderNo, keyword);
        }
        if (status != null) {
            wrapper.eq(PurchaseOrder::getStatus, status);
        }
        wrapper.orderByDesc(PurchaseOrder::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void createPurchaseOrder(PurchaseOrderDTO dto) {
        PurchaseOrder order = new PurchaseOrder();
        BeanUtils.copyProperties(dto, order);
        order.setOrderNo(generateOrderNo());
        order.setStatus(0); // 草稿

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderDTO.PurchaseOrderItemDTO itemDTO : dto.getItems()) {
            if (itemDTO.getAmount() == null && itemDTO.getUnitPrice() != null && itemDTO.getQuantity() != null) {
                itemDTO.setAmount(itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            }
            if (itemDTO.getAmount() != null) {
                totalAmount = totalAmount.add(itemDTO.getAmount());
            }
        }
        order.setTotalAmount(totalAmount);
        save(order);

        // 保存明细
        for (PurchaseOrderDTO.PurchaseOrderItemDTO itemDTO : dto.getItems()) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setPurchaseOrderId(order.getId());
            purchaseOrderItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void confirmOrder(Long id) {
        PurchaseOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "采购单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_STATUS_ERROR, "只有草稿状态可以确认");
        }
        order.setStatus(1); // 已确认
        updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        PurchaseOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "采购单不存在");
        }
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_STATUS_ERROR, "当前状态不能取消");
        }
        order.setStatus(4); // 已取消
        updateById(order);
    }

    private String generateOrderNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PO-" + dateStr + "-";
        PurchaseOrder maxOrder = getOne(new LambdaQueryWrapper<PurchaseOrder>()
                .likeRight(PurchaseOrder::getOrderNo, prefix)
                .orderByDesc(PurchaseOrder::getOrderNo)
                .last("LIMIT 1"));
        int seq = 1;
        if (maxOrder != null && maxOrder.getOrderNo() != null && maxOrder.getOrderNo().startsWith(prefix)) {
            try {
                seq = Integer.parseInt(maxOrder.getOrderNo().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("PO-%s-%03d", dateStr, seq);
    }
}
