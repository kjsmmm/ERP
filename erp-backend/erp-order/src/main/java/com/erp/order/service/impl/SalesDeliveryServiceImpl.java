package com.erp.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.order.dto.SalesDeliveryDTO;
import com.erp.order.entity.OrderItem;
import com.erp.order.entity.SalesDelivery;
import com.erp.order.entity.SalesDeliveryItem;
import com.erp.order.entity.SalesOrder;
import com.erp.order.mapper.OrderItemMapper;
import com.erp.order.mapper.OrderMapper;
import com.erp.order.mapper.SalesDeliveryItemMapper;
import com.erp.order.mapper.SalesDeliveryMapper;
import com.erp.order.service.SalesDeliveryService;
import com.erp.product.entity.Product;
import com.erp.product.mapper.ProductMapper;
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
public class SalesDeliveryServiceImpl extends ServiceImpl<SalesDeliveryMapper, SalesDelivery> implements SalesDeliveryService {

    private final SalesDeliveryItemMapper deliveryItemMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final ApplicationContext applicationContext;

    @Override
    public IPage<SalesDelivery> getDeliveryPage(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<SalesDelivery> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SalesDelivery> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SalesDelivery::getDeliveryNo, keyword);
        }
        if (status != null) {
            wrapper.eq(SalesDelivery::getStatus, status);
        }
        wrapper.orderByDesc(SalesDelivery::getCreatedAt);
        IPage<SalesDelivery> result = page(page, wrapper);
        result.getRecords().forEach(this::fillNames);
        return result;
    }

    @Override
    @Transactional
    public void createDelivery(SalesDeliveryDTO dto) {
        SalesOrder order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "销售订单不存在");
        }

        // 查询订单明细
        List<OrderItem> orderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        Map<Long, Integer> orderQtyMap = orderItems.stream()
                .collect(Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity, Integer::sum));

        // 查询已发货数量
        List<SalesDelivery> existingDeliveries = list(new LambdaQueryWrapper<SalesDelivery>()
                .eq(SalesDelivery::getOrderId, order.getId())
                .ne(SalesDelivery::getStatus, 0));
        Map<Long, Integer> deliveredQtyMap = new HashMap<>();
        for (SalesDelivery delivery : existingDeliveries) {
            List<SalesDeliveryItem> items = deliveryItemMapper.selectList(
                    new LambdaQueryWrapper<SalesDeliveryItem>().eq(SalesDeliveryItem::getDeliveryId, delivery.getId()));
            for (SalesDeliveryItem item : items) {
                deliveredQtyMap.merge(item.getProductId(), item.getQuantity(), Integer::sum);
            }
        }

        // 校验发货数量不超过订单剩余数量
        for (SalesDeliveryDTO.DeliveryItemDTO itemDTO : dto.getItems()) {
            int orderQty = orderQtyMap.getOrDefault(itemDTO.getProductId(), 0);
            int deliveredQty = deliveredQtyMap.getOrDefault(itemDTO.getProductId(), 0);
            if (deliveredQty + itemDTO.getQuantity() > orderQty) {
                throw new BusinessException(ErrorCode.DELIVERY_QTY_EXCEEDED, "发货数量超出订单数量");
            }
        }

        SalesDelivery delivery = new SalesDelivery();
        BeanUtils.copyProperties(dto, delivery);
        delivery.setDeliveryNo(generateDeliveryNo());
        delivery.setStatus(0);
        delivery.setCustomerId(order.getCustomerId());
        save(delivery);

        for (SalesDeliveryDTO.DeliveryItemDTO itemDTO : dto.getItems()) {
            SalesDeliveryItem item = new SalesDeliveryItem();
            item.setDeliveryId(delivery.getId());
            item.setProductId(itemDTO.getProductId());
            item.setQuantity(itemDTO.getQuantity());
            deliveryItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void pick(Long id) {
        SalesDelivery delivery = getById(id);
        if (delivery == null) {
            throw new BusinessException(ErrorCode.DELIVERY_NOT_FOUND, "发货单不存在");
        }
        if (delivery.getStatus() != 0) {
            throw new BusinessException(ErrorCode.DELIVERY_STATUS_ERROR, "只有草稿状态可以拣货");
        }
        delivery.setStatus(1);
        updateById(delivery);
    }

    @Override
    @Transactional
    public void shipOut(Long id) {
        SalesDelivery delivery = getById(id);
        if (delivery == null) {
            throw new BusinessException(ErrorCode.DELIVERY_NOT_FOUND, "发货单不存在");
        }
        if (delivery.getStatus() != 1) {
            throw new BusinessException(ErrorCode.DELIVERY_STATUS_ERROR, "只有待出库状态可以出库");
        }

        List<SalesDeliveryItem> items = deliveryItemMapper.selectList(
                new LambdaQueryWrapper<SalesDeliveryItem>().eq(SalesDeliveryItem::getDeliveryId, id));

        // 跨模块调用库存服务出库
        stockOut(items, delivery.getWarehouseId(), delivery.getDeliveryNo());

        delivery.setStatus(2);
        updateById(delivery);

        // 自动创建应收单（跨模块调用）
        createArRecord(delivery, items);
    }

    @Override
    @Transactional
    public void sign(Long id) {
        SalesDelivery delivery = getById(id);
        if (delivery == null) {
            throw new BusinessException(ErrorCode.DELIVERY_NOT_FOUND, "发货单不存在");
        }
        if (delivery.getStatus() != 2) {
            throw new BusinessException(ErrorCode.DELIVERY_STATUS_ERROR, "只有已出库状态可以签收");
        }
        delivery.setStatus(3);
        updateById(delivery);
    }

    private void stockOut(List<SalesDeliveryItem> items, Long warehouseId, String deliveryNo) {
        try {
            Object stockService = applicationContext.getBean("stockServiceImpl");
            java.lang.reflect.Method stockOutMethod = stockService.getClass()
                    .getMethod("stockOut", Class.forName("com.erp.inventory.dto.StockOutDTO"));

            for (SalesDeliveryItem item : items) {
                Object dto = Class.forName("com.erp.inventory.dto.StockOutDTO").getDeclaredConstructor().newInstance();
                dto.getClass().getMethod("setProductId", Long.class).invoke(dto, item.getProductId());
                dto.getClass().getMethod("setWarehouseId", Long.class).invoke(dto, warehouseId);
                dto.getClass().getMethod("setQuantity", BigDecimal.class).invoke(dto, BigDecimal.valueOf(item.getQuantity()));
                dto.getClass().getMethod("setReferenceNo", String.class).invoke(dto, deliveryNo);
                dto.getClass().getMethod("setReferenceType", String.class).invoke(dto, "SALES_DELIVERY");
                dto.getClass().getMethod("setRemark", String.class).invoke(dto, "销售发货出库");
                stockOutMethod.invoke(stockService, dto);
            }
        } catch (Exception ignored) {
            // 库存模块未部署时跳过
        }
    }

    private void createArRecord(SalesDelivery delivery, List<SalesDeliveryItem> items) {
        try {
            // 计算发货金额（订单明细单价 × 发货数量）
            List<OrderItem> orderItems = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, delivery.getOrderId()));
            Map<Long, java.math.BigDecimal> priceMap = orderItems.stream()
                    .collect(java.util.stream.Collectors.toMap(OrderItem::getProductId, OrderItem::getUnitPrice, (a, b) -> a));

            java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
            for (SalesDeliveryItem item : items) {
                java.math.BigDecimal unitPrice = priceMap.getOrDefault(item.getProductId(), java.math.BigDecimal.ZERO);
                totalAmount = totalAmount.add(unitPrice.multiply(java.math.BigDecimal.valueOf(item.getQuantity())));
            }

            Object arService = applicationContext.getBean("arRecordServiceImpl");
            java.lang.reflect.Method createMethod = arService.getClass()
                    .getMethod("createFromDelivery", Long.class, Long.class, Long.class, java.math.BigDecimal.class);
            createMethod.invoke(arService, delivery.getId(), delivery.getOrderId(), delivery.getCustomerId(), totalAmount);
        } catch (Exception ignored) {
            // 财务模块未部署时跳过
        }
    }

    private String generateDeliveryNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "DEL-" + dateStr + "-";
        SalesDelivery max = getOne(new LambdaQueryWrapper<SalesDelivery>()
                .likeRight(SalesDelivery::getDeliveryNo, prefix)
                .orderByDesc(SalesDelivery::getDeliveryNo)
                .last("LIMIT 1"));
        int seq = 1;
        if (max != null && max.getDeliveryNo() != null && max.getDeliveryNo().startsWith(prefix)) {
            try {
                seq = Integer.parseInt(max.getDeliveryNo().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("DEL-%s-%03d", dateStr, seq);
    }

    private void fillNames(SalesDelivery delivery) {
        if (delivery.getOrderId() != null) {
            SalesOrder order = orderMapper.selectById(delivery.getOrderId());
            if (order != null) delivery.setOrderNo(order.getOrderNo());
        }
    }
}
