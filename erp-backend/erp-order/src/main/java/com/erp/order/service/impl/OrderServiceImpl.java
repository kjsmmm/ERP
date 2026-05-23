package com.erp.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.context.UserIdProvider;
import com.erp.common.workflow.ProcessInstanceVO;
import com.erp.common.workflow.WorkflowService;
import com.erp.customer.entity.Customer;
import com.erp.customer.mapper.CustomerMapper;
import com.erp.order.dto.OrderDTO;
import com.erp.order.dto.OrderItemDTO;
import com.erp.order.dto.OrderQueryDTO;
import com.erp.order.entity.OrderItem;
import com.erp.order.entity.SalesDelivery;
import com.erp.order.entity.SalesDeliveryItem;
import com.erp.order.entity.SalesOrder;
import com.erp.order.mapper.OrderItemMapper;
import com.erp.order.mapper.OrderMapper;
import com.erp.order.mapper.SalesDeliveryItemMapper;
import com.erp.order.mapper.SalesDeliveryMapper;
import com.erp.order.service.CustomerPriceService;
import com.erp.order.service.OrderService;
import com.erp.order.vo.OrderDetailVO;
import com.erp.product.entity.Product;
import com.erp.product.mapper.ProductMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, SalesOrder> implements OrderService {

    private final OrderItemMapper orderItemMapper;
    private final CustomerMapper customerMapper;
    private final ProductMapper productMapper;
    private final WorkflowService workflowService;
    private final CustomerPriceService customerPriceService;
    private final ObjectMapper objectMapper;
    private final UserIdProvider userIdProvider;
    private final SalesDeliveryMapper salesDeliveryMapper;
    private final SalesDeliveryItemMapper salesDeliveryItemMapper;

    /** 合法状态流转 Map<State, Set<State>> */
    private static final Map<Integer, Set<Integer>> TRANSITIONS = new HashMap<>();

    static {
        TRANSITIONS.put(1, Set.of(2, 6));          // 草稿 → 已确认、已取消
        TRANSITIONS.put(2, Set.of(3, 6, 8));       // 已确认 → 生产中、已取消、变更审批中
        TRANSITIONS.put(3, Set.of(4, 7));          // 生产中 → 已完成、已暂停
        TRANSITIONS.put(4, Set.of(5));             // 已完成 → 已关闭
        TRANSITIONS.put(7, Set.of(3));             // 已暂停 → 生产中
        TRANSITIONS.put(8, Set.of(2));             // 变更审批中 → 已确认（审批通过/驳回均回到已确认）
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(OrderDTO dto) {
        SalesOrder order = new SalesOrder();
        BeanUtils.copyProperties(dto, order);
        order.setOrderNo(generateOrderNo());
        order.setStatus(1); // 草稿

        // 计算总金额
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < dto.getItems().size(); i++) {
            OrderItemDTO itemDTO = dto.getItems().get(i);
            OrderItem item = new OrderItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setSortOrder(itemDTO.getSortOrder() != null ? itemDTO.getSortOrder() : i);

            // 单价自动填充：客户专属价格 > 产品标准售价
            BigDecimal unitPrice = itemDTO.getUnitPrice();
            if (unitPrice == null) {
                unitPrice = resolvePrice(dto.getCustomerId(), itemDTO.getProductId());
                item.setUnitPrice(unitPrice);
            }

            item.setSubtotal(itemDTO.getQuantity().multiply(unitPrice));
            total = total.add(item.getSubtotal());
            items.add(item);
        }
        order.setTotalAmount(total);

        save(order);

        // 保存明细
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        return order.getId();
    }

    @Override
    public IPage<SalesOrder> getOrderPage(OrderQueryDTO queryDTO) {
        Page<SalesOrder> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();

        wrapper.and(StringUtils.hasText(queryDTO.getKeyword()), w ->
                w.like(SalesOrder::getOrderNo, queryDTO.getKeyword())
                 .or()
                 .like(SalesOrder::getCustomerName, queryDTO.getKeyword()));
        wrapper.eq(queryDTO.getStatus() != null, SalesOrder::getStatus, queryDTO.getStatus());
        wrapper.eq(queryDTO.getCustomerId() != null, SalesOrder::getCustomerId, queryDTO.getCustomerId());
        wrapper.ge(queryDTO.getStartDate() != null, SalesOrder::getCreatedAt, queryDTO.getStartDate());
        wrapper.le(queryDTO.getEndDate() != null, SalesOrder::getCreatedAt, queryDTO.getEndDate().plusDays(1));
        wrapper.orderByDesc(SalesOrder::getCreatedAt);

        return page(page, wrapper);
    }

    @Override
    public OrderDetailVO getOrderDetail(Long id) {
        SalesOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo);

        // 加载客户信息
        if (order.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(order.getCustomerId());
            if (customer != null) {
                vo.setCustomerName(customer.getCustomerName());
                vo.setCustomerCode(customer.getCustomerCode());
            }
        }

        // 加载订单明细
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, id)
                        .orderByAsc(OrderItem::getSortOrder));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                item.setProductName(product.getProductName());
                item.setProductCode(product.getProductCode());
                item.setUnit(product.getUnit());
                item.setSpec(product.getSpec());
            }
        }
        vo.setItems(items);

        // 审批状态
        if (order.getProcessInstanceId() != null) {
            vo.setProcessInstanceId(order.getProcessInstanceId());
            ProcessInstanceVO processInfo = workflowService.getProcessInstance(order.getProcessInstanceId());
            if (processInfo != null) {
                vo.setApprovalStatus(processInfo.isEnded() ? "completed" : "pending");
            }
        }

        // 加载发货状态
        List<SalesDelivery> deliveries = salesDeliveryMapper.selectList(
                new LambdaQueryWrapper<SalesDelivery>()
                        .eq(SalesDelivery::getOrderId, id)
                        .ne(SalesDelivery::getStatus, 0));
        int totalDeliveredQty = 0;
        for (SalesDelivery delivery : deliveries) {
            List<SalesDeliveryItem> deliveryItems = salesDeliveryItemMapper.selectList(
                    new LambdaQueryWrapper<SalesDeliveryItem>()
                            .eq(SalesDeliveryItem::getDeliveryId, delivery.getId()));
            for (SalesDeliveryItem di : deliveryItems) {
                totalDeliveredQty += di.getQuantity() != null ? di.getQuantity() : 0;
            }
        }
        int totalOrderQty = items.stream().mapToInt(OrderItem::getQuantity).sum();
        if (totalDeliveredQty > 0 && totalDeliveredQty >= totalOrderQty) {
            vo.setDeliveryStatus(2);
        } else if (totalDeliveredQty > 0) {
            vo.setDeliveryStatus(1);
        } else {
            vo.setDeliveryStatus(0);
        }
        vo.setTotalDeliveredQty(totalDeliveredQty);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(Long id, OrderDTO dto) {
        SalesOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "仅草稿或已确认状态可编辑");
        }

        // 已确认订单需要走审批流程
        if (order.getStatus() == 2) {
            try {
                String pendingJson = objectMapper.writeValueAsString(dto);
                order.setPendingData(pendingJson);
                order.setStatus(8); // 变更审批中

                // 发起审批流程
                String processInstanceId = workflowService.startProcess(
                        "order-change-approval",
                        String.valueOf(id),
                        String.valueOf(userIdProvider.getCurrentUserId()),
                        Map.of("orderId", id)
                );
                order.setProcessInstanceId(processInstanceId);
                updateById(order);
            } catch (JsonProcessingException e) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "序列化变更数据失败");
            }
            return;
        }

        // 草稿状态直接更新
        applyOrderChanges(order, dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long id) {
        SalesOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "仅草稿状态可删除");
        }
        orderItemMapper.delete(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long id) {
        changeStatus(id, 2, "确认");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id) {
        changeStatus(id, 6, "取消");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long id) {
        changeStatus(id, 4, "完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeOrder(Long id) {
        changeStatus(id, 5, "关闭");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyOrderChange(String processInstanceId) {
        SalesOrder order = getOne(new LambdaQueryWrapper<SalesOrder>()
                .eq(SalesOrder::getProcessInstanceId, processInstanceId));
        if (order == null) {
            return;
        }
        if (order.getPendingData() == null) {
            return;
        }
        try {
            OrderDTO dto = objectMapper.readValue(order.getPendingData(), new TypeReference<>() {});
            applyOrderChanges(order, dto);
            order.setProcessInstanceId(null);
            order.setPendingData(null);
            order.setStatus(2); // 回到已确认
            updateById(order);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "解析变更数据失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void discardOrderChange(String processInstanceId) {
        SalesOrder order = getOne(new LambdaQueryWrapper<SalesOrder>()
                .eq(SalesOrder::getProcessInstanceId, processInstanceId));
        if (order == null) {
            return;
        }
        order.setProcessInstanceId(null);
        order.setPendingData(null);
        order.setStatus(2); // 回到已确认
        updateById(order);
    }

    private void applyOrderChanges(SalesOrder order, OrderDTO dto) {
        order.setCustomerId(dto.getCustomerId());
        order.setDeliveryDate(dto.getDeliveryDate());
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setContactPhone(dto.getContactPhone());
        order.setRemark(dto.getRemark());

        // 删除旧明细，插入新明细
        orderItemMapper.delete(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < dto.getItems().size(); i++) {
            OrderItemDTO itemDTO = dto.getItems().get(i);
            OrderItem item = new OrderItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setOrderId(order.getId());
            item.setSortOrder(itemDTO.getSortOrder() != null ? itemDTO.getSortOrder() : i);
            item.setSubtotal(itemDTO.getQuantity().multiply(itemDTO.getUnitPrice()));
            total = total.add(item.getSubtotal());
            orderItemMapper.insert(item);
        }
        order.setTotalAmount(total);
        updateById(order);
    }

    private void changeStatus(Long id, Integer targetStatus, String action) {
        SalesOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        Set<Integer> allowed = TRANSITIONS.getOrDefault(order.getStatus(), Set.of());
        if (!allowed.contains(targetStatus)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    String.format("不允许从%s状态%s", statusName(order.getStatus()), action));
        }
        order.setStatus(targetStatus);
        updateById(order);
    }

    private String statusName(Integer status) {
        return switch (status) {
            case 1 -> "草稿";
            case 2 -> "已确认";
            case 3 -> "生产中";
            case 4 -> "已完成";
            case 5 -> "已关闭";
            case 6 -> "已取消";
            case 7 -> "已暂停";
            case 8 -> "变更审批中";
            default -> "未知";
        };
    }

    private BigDecimal resolvePrice(Long customerId, Long productId) {
        // 客户专属价格 > 产品标准售价
        if (customerId != null) {
            BigDecimal customerPrice = customerPriceService.getPrice(customerId, productId);
            if (customerPrice != null) {
                return customerPrice;
            }
        }
        Product product = productMapper.selectById(productId);
        if (product != null && product.getStandardPrice() != null) {
            return product.getStandardPrice();
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST, "产品价格未设置");
    }

    private String generateOrderNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "SO-" + dateStr + "-";
        SalesOrder maxOrder = getOne(new LambdaQueryWrapper<SalesOrder>()
                .likeRight(SalesOrder::getOrderNo, prefix)
                .orderByDesc(SalesOrder::getOrderNo)
                .last("LIMIT 1"));
        int seq = 1;
        if (maxOrder != null && maxOrder.getOrderNo() != null && maxOrder.getOrderNo().startsWith(prefix)) {
            try {
                seq = Integer.parseInt(maxOrder.getOrderNo().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("SO-%s-%03d", dateStr, seq);
    }
}
