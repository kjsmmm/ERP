package com.erp.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.workflow.WorkflowService;
import com.erp.order.dto.SalesReturnDTO;
import com.erp.order.entity.SalesDelivery;
import com.erp.order.entity.SalesDeliveryItem;
import com.erp.order.entity.SalesReturn;
import com.erp.order.entity.SalesReturnItem;
import com.erp.order.mapper.SalesDeliveryItemMapper;
import com.erp.order.mapper.SalesDeliveryMapper;
import com.erp.order.mapper.SalesReturnItemMapper;
import com.erp.order.mapper.SalesReturnMapper;
import com.erp.order.service.SalesReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesReturnServiceImpl extends ServiceImpl<SalesReturnMapper, SalesReturn> implements SalesReturnService {

    private final SalesReturnItemMapper returnItemMapper;
    private final SalesDeliveryMapper deliveryMapper;
    private final SalesDeliveryItemMapper deliveryItemMapper;
    private final WorkflowService workflowService;
    private final ApplicationContext applicationContext;

    @Override
    public IPage<SalesReturn> getReturnPage(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<SalesReturn> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SalesReturn> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SalesReturn::getReturnNo, keyword);
        }
        if (status != null) {
            wrapper.eq(SalesReturn::getStatus, status);
        }
        wrapper.orderByDesc(SalesReturn::getCreatedAt);
        IPage<SalesReturn> result = page(page, wrapper);
        result.getRecords().forEach(this::fillNames);
        return result;
    }

    @Override
    @Transactional
    public void createReturn(SalesReturnDTO dto) {
        SalesDelivery delivery = deliveryMapper.selectById(dto.getDeliveryId());
        if (delivery == null) {
            throw new BusinessException(ErrorCode.DELIVERY_NOT_FOUND, "发货单不存在");
        }

        // 查询已发货明细
        List<SalesDeliveryItem> deliveryItems = deliveryItemMapper.selectList(
                new LambdaQueryWrapper<SalesDeliveryItem>().eq(SalesDeliveryItem::getDeliveryId, delivery.getId()));
        Map<Long, Integer> deliveredQtyMap = deliveryItems.stream()
                .collect(Collectors.toMap(SalesDeliveryItem::getProductId, SalesDeliveryItem::getQuantity, Integer::sum));

        // 查询已退货数量
        List<SalesReturn> existingReturns = list(new LambdaQueryWrapper<SalesReturn>()
                .eq(SalesReturn::getDeliveryId, delivery.getId())
                .ne(SalesReturn::getStatus, 3));
        Map<Long, Integer> returnedQtyMap = new HashMap<>();
        for (SalesReturn sr : existingReturns) {
            List<SalesReturnItem> items = returnItemMapper.selectList(
                    new LambdaQueryWrapper<SalesReturnItem>().eq(SalesReturnItem::getReturnId, sr.getId()));
            for (SalesReturnItem item : items) {
                returnedQtyMap.merge(item.getProductId(), item.getQuantity(), Integer::sum);
            }
        }

        // 校验退货数量不超过发货数量
        for (SalesReturnDTO.ReturnItemDTO itemDTO : dto.getItems()) {
            int deliveredQty = deliveredQtyMap.getOrDefault(itemDTO.getProductId(), 0);
            int returnedQty = returnedQtyMap.getOrDefault(itemDTO.getProductId(), 0);
            if (returnedQty + itemDTO.getQuantity() > deliveredQty) {
                throw new BusinessException(ErrorCode.RETURN_QTY_EXCEEDED, "退货数量超出发货数量");
            }
        }

        SalesReturn salesReturn = new SalesReturn();
        salesReturn.setReturnNo(generateReturnNo());
        salesReturn.setDeliveryId(dto.getDeliveryId());
        salesReturn.setOrderId(delivery.getOrderId());
        salesReturn.setCustomerId(delivery.getCustomerId());
        salesReturn.setReturnReason(dto.getReturnReason());
        salesReturn.setWarehouseId(dto.getWarehouseId());
        salesReturn.setRemark(dto.getRemark());
        salesReturn.setStatus(0);
        save(salesReturn);

        for (SalesReturnDTO.ReturnItemDTO itemDTO : dto.getItems()) {
            SalesReturnItem item = new SalesReturnItem();
            item.setReturnId(salesReturn.getId());
            item.setProductId(itemDTO.getProductId());
            item.setQuantity(itemDTO.getQuantity());
            item.setReason(itemDTO.getReason());
            returnItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void submitForApproval(Long id) {
        SalesReturn salesReturn = getById(id);
        if (salesReturn == null) {
            throw new BusinessException(ErrorCode.RETURN_NOT_FOUND, "退货单不存在");
        }
        if (salesReturn.getStatus() != 0) {
            throw new BusinessException(ErrorCode.RETURN_STATUS_ERROR, "只有待审批状态可以提交");
        }

        String processInstanceId = workflowService.startProcess(
                "sales-return-approval",
                String.valueOf(id),
                String.valueOf(1L),
                null
        );

        salesReturn.setProcessInstanceId(processInstanceId);
        salesReturn.setStatus(1);
        updateById(salesReturn);
    }

    @Override
    @Transactional
    public void approveCallback(String processInstanceId, boolean approved) {
        SalesReturn salesReturn = getOne(new LambdaQueryWrapper<SalesReturn>()
                .eq(SalesReturn::getProcessInstanceId, processInstanceId));
        if (salesReturn == null) return;

        if (approved) {
            salesReturn.setStatus(2);
            updateById(salesReturn);
        } else {
            salesReturn.setStatus(3);
            updateById(salesReturn);
        }
    }

    @Override
    @Transactional
    public void receive(Long id) {
        SalesReturn salesReturn = getById(id);
        if (salesReturn == null) {
            throw new BusinessException(ErrorCode.RETURN_NOT_FOUND, "退货单不存在");
        }
        if (salesReturn.getStatus() != 2) {
            throw new BusinessException(ErrorCode.RETURN_STATUS_ERROR, "只有已通过状态可以入库");
        }

        List<SalesReturnItem> items = returnItemMapper.selectList(
                new LambdaQueryWrapper<SalesReturnItem>().eq(SalesReturnItem::getReturnId, id));

        // 跨模块调用库存服务入库
        stockIn(items, salesReturn.getWarehouseId(), salesReturn.getReturnNo());

        salesReturn.setStatus(4);
        updateById(salesReturn);
    }

    private void stockIn(List<SalesReturnItem> items, Long warehouseId, String returnNo) {
        try {
            Object stockService = applicationContext.getBean("stockServiceImpl");
            java.lang.reflect.Method stockInMethod = stockService.getClass()
                    .getMethod("stockIn", Class.forName("com.erp.inventory.dto.StockInDTO"));

            for (SalesReturnItem item : items) {
                Object dto = Class.forName("com.erp.inventory.dto.StockInDTO").getDeclaredConstructor().newInstance();
                dto.getClass().getMethod("setProductId", Long.class).invoke(dto, item.getProductId());
                dto.getClass().getMethod("setWarehouseId", Long.class).invoke(dto, warehouseId);
                dto.getClass().getMethod("setQuantity", BigDecimal.class).invoke(dto, BigDecimal.valueOf(item.getQuantity()));
                dto.getClass().getMethod("setReferenceNo", String.class).invoke(dto, returnNo);
                dto.getClass().getMethod("setReferenceType", String.class).invoke(dto, "SALES_RETURN");
                dto.getClass().getMethod("setRemark", String.class).invoke(dto, "销售退货入库");
                stockInMethod.invoke(stockService, dto);
            }
        } catch (Exception ignored) {
            // 库存模块未部署时跳过
        }
    }

    private String generateReturnNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "RET-" + dateStr + "-";
        SalesReturn max = getOne(new LambdaQueryWrapper<SalesReturn>()
                .likeRight(SalesReturn::getReturnNo, prefix)
                .orderByDesc(SalesReturn::getReturnNo)
                .last("LIMIT 1"));
        int seq = 1;
        if (max != null && max.getReturnNo() != null && max.getReturnNo().startsWith(prefix)) {
            try {
                seq = Integer.parseInt(max.getReturnNo().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("RET-%s-%03d", dateStr, seq);
    }

    private void fillNames(SalesReturn salesReturn) {
        if (salesReturn.getDeliveryId() != null) {
            SalesDelivery delivery = deliveryMapper.selectById(salesReturn.getDeliveryId());
            if (delivery != null) salesReturn.setDeliveryNo(delivery.getDeliveryNo());
        }
    }
}
