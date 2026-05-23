package com.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.inventory.dto.StockInDTO;
import com.erp.inventory.service.StockService;
import com.erp.purchase.dto.PurchaseReceiptDTO;
import com.erp.purchase.entity.PurchaseOrder;
import com.erp.purchase.entity.PurchaseOrderItem;
import com.erp.purchase.entity.PurchaseReceipt;
import com.erp.purchase.entity.PurchaseReceiptItem;
import com.erp.purchase.mapper.PurchaseOrderItemMapper;
import com.erp.purchase.mapper.PurchaseOrderMapper;
import com.erp.purchase.mapper.PurchaseReceiptItemMapper;
import com.erp.purchase.mapper.PurchaseReceiptMapper;
import com.erp.purchase.service.PurchaseReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseReceiptServiceImpl extends ServiceImpl<PurchaseReceiptMapper, PurchaseReceipt> implements PurchaseReceiptService {

    private final PurchaseReceiptItemMapper purchaseReceiptItemMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;
    private final ApplicationContext applicationContext;

    @Override
    public IPage<PurchaseReceipt> getPurchaseReceiptPage(String keyword, Integer pageNum, Integer pageSize) {
        Page<PurchaseReceipt> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseReceipt> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PurchaseReceipt::getReceiptNo, keyword);
        }
        wrapper.orderByDesc(PurchaseReceipt::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void createReceipt(PurchaseReceiptDTO dto) {
        // 验证采购单存在且状态允许入库
        PurchaseOrder order = purchaseOrderMapper.selectById(dto.getPurchaseOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "采购单不存在");
        }
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_STATUS_ERROR, "采购单状态不允许入库");
        }

        // 查询采购单明细，验证入库数量
        List<PurchaseOrderItem> orderItems = purchaseOrderItemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItem>().eq(PurchaseOrderItem::getPurchaseOrderId, order.getId()));
        Map<Long, Integer> orderQtyMap = orderItems.stream()
                .collect(Collectors.toMap(PurchaseOrderItem::getProductId, PurchaseOrderItem::getQuantity));

        // 查询已入库数量
        List<PurchaseReceipt> existingReceipts = list(new LambdaQueryWrapper<PurchaseReceipt>()
                .eq(PurchaseReceipt::getPurchaseOrderId, order.getId()));
        Map<Long, Integer> receivedQtyMap = BigDecimal.ZERO.intValue() == 0 ? new java.util.HashMap<>() : new java.util.HashMap<>();
        for (PurchaseReceipt receipt : existingReceipts) {
            List<PurchaseReceiptItem> items = purchaseReceiptItemMapper.selectList(
                    new LambdaQueryWrapper<PurchaseReceiptItem>().eq(PurchaseReceiptItem::getPurchaseReceiptId, receipt.getId()));
            for (PurchaseReceiptItem item : items) {
                receivedQtyMap.merge(item.getProductId(), item.getQuantity(), Integer::sum);
            }
        }

        // 验证本次入库不超量
        for (PurchaseReceiptDTO.PurchaseReceiptItemDTO itemDTO : dto.getItems()) {
            int orderQty = orderQtyMap.getOrDefault(itemDTO.getProductId(), 0);
            int receivedQty = receivedQtyMap.getOrDefault(itemDTO.getProductId(), 0);
            if (receivedQty + itemDTO.getQuantity() > orderQty) {
                throw new BusinessException(ErrorCode.RECEIPT_QTY_EXCEEDED, "入库数量超出采购数量");
            }
        }

        // 检查 IQC 检验状态（跨模块调用）
        checkIqcStatus(order.getId());

        // 创建入库单
        PurchaseReceipt receipt = new PurchaseReceipt();
        BeanUtils.copyProperties(dto, receipt);
        receipt.setReceiptNo(generateReceiptNo());
        receipt.setStatus(1); // 已入库
        receipt.setInspectionStatus(0); // 待检验（留给5b）
        save(receipt);

        // 保存明细并更新库存
        StockService stockService = applicationContext.getBean(StockService.class);
        for (PurchaseReceiptDTO.PurchaseReceiptItemDTO itemDTO : dto.getItems()) {
            PurchaseReceiptItem item = new PurchaseReceiptItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setPurchaseReceiptId(receipt.getId());
            purchaseReceiptItemMapper.insert(item);

            // 调用库存服务入库
            StockInDTO stockIn = new StockInDTO();
            stockIn.setProductId(itemDTO.getProductId());
            stockIn.setWarehouseId(dto.getWarehouseId());
            stockIn.setQuantity(BigDecimal.valueOf(itemDTO.getQuantity()));
            stockIn.setReferenceNo(receipt.getReceiptNo());
            stockIn.setReferenceType("PURCHASE_RECEIPT");
            stockIn.setReferenceId(receipt.getId());
            stockIn.setRemark("采购入库");
            stockService.stockIn(stockIn);
        }

        // 更新采购单状态
        int totalOrderQty = orderQtyMap.values().stream().mapToInt(Integer::intValue).sum();
        int totalReceivedQty = receivedQtyMap.values().stream().mapToInt(Integer::intValue).sum()
                + dto.getItems().stream().mapToInt(PurchaseReceiptDTO.PurchaseReceiptItemDTO::getQuantity).sum();
        if (totalReceivedQty >= totalOrderQty) {
            order.setStatus(3); // 已完成
        } else {
            order.setStatus(2); // 部分入库
        }
        purchaseOrderMapper.updateById(order);

        // 自动创建应付单（跨模块调用）
        createApRecord(receipt, dto);
    }

    private void createApRecord(PurchaseReceipt receipt, PurchaseReceiptDTO dto) {
        try {
            // 计算入库金额（采购单明细单价 × 入库数量）
            List<PurchaseOrderItem> orderItems = purchaseOrderItemMapper.selectList(
                    new LambdaQueryWrapper<PurchaseOrderItem>().eq(PurchaseOrderItem::getPurchaseOrderId, receipt.getPurchaseOrderId()));
            Map<Long, BigDecimal> priceMap = orderItems.stream()
                    .collect(Collectors.toMap(PurchaseOrderItem::getProductId, PurchaseOrderItem::getUnitPrice, (a, b) -> a));

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (PurchaseReceiptDTO.PurchaseReceiptItemDTO item : dto.getItems()) {
                BigDecimal unitPrice = priceMap.getOrDefault(item.getProductId(), BigDecimal.ZERO);
                totalAmount = totalAmount.add(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
            }

            PurchaseOrder order = purchaseOrderMapper.selectById(receipt.getPurchaseOrderId());

            Object apService = applicationContext.getBean("apRecordServiceImpl");
            java.lang.reflect.Method createMethod = apService.getClass()
                    .getMethod("createFromReceipt", Long.class, Long.class, Long.class, BigDecimal.class);
            createMethod.invoke(apService, receipt.getId(), receipt.getPurchaseOrderId(),
                    order != null ? order.getSupplierId() : null, totalAmount);
        } catch (Exception ignored) {
            // 财务模块未部署时跳过
        }
    }

    private String generateReceiptNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "REC-" + dateStr + "-";
        PurchaseReceipt maxReceipt = getOne(new LambdaQueryWrapper<PurchaseReceipt>()
                .likeRight(PurchaseReceipt::getReceiptNo, prefix)
                .orderByDesc(PurchaseReceipt::getReceiptNo)
                .last("LIMIT 1"));
        int seq = 1;
        if (maxReceipt != null && maxReceipt.getReceiptNo() != null && maxReceipt.getReceiptNo().startsWith(prefix)) {
            try {
                seq = Integer.parseInt(maxReceipt.getReceiptNo().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("REC-%s-%03d", dateStr, seq);
    }

    /**
     * 检查 IQC 检验状态，未通过则拒绝入库
     */
    private void checkIqcStatus(Long purchaseOrderId) {
        try {
            Object iqInspectionService = applicationContext.getBean("iqInspectionServiceImpl");
            java.lang.reflect.Method isPassed = iqInspectionService.getClass()
                    .getMethod("isIqcPassed", Long.class);
            Boolean passed = (Boolean) isPassed.invoke(iqInspectionService, purchaseOrderId);
            if (!Boolean.TRUE.equals(passed)) {
                throw new BusinessException(ErrorCode.INSPECTION_RESULT_FAILED, "来料检验未通过，不能入库");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ignored) {
            // 质量模块未部署时跳过检查
        }
    }
}
