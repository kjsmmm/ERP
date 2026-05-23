package com.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.production.dto.WorkOrderDTO;
import com.erp.production.entity.*;
import com.erp.production.mapper.*;
import com.erp.production.service.WorkOrderService;
import com.erp.product.entity.BomItem;
import com.erp.product.entity.Product;
import com.erp.product.mapper.BomItemMapper;
import com.erp.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {

    private final WorkOrderStepMapper workOrderStepMapper;
    private final WorkReportMapper workReportMapper;
    private final ProcessRouteMapper processRouteMapper;
    private final ProcessStepMapper processStepMapper;
    private final ProductMapper productMapper;
    private final WorkshopMapper workshopMapper;
    private final BomItemMapper bomItemMapper;
    private final ApplicationContext applicationContext;

    @Override
    public IPage<WorkOrder> getOrderPage(Long workshopId, Integer status, Integer pageNum, Integer pageSize) {
        Page<WorkOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(workshopId != null, WorkOrder::getWorkshopId, workshopId);
        wrapper.eq(status != null, WorkOrder::getStatus, status);
        wrapper.orderByDesc(WorkOrder::getCreatedAt);
        IPage<WorkOrder> result = page(page, wrapper);
        result.getRecords().forEach(this::fillNames);
        return result;
    }

    @Override
    public WorkOrder getDetail(Long id) {
        WorkOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND, "工单不存在");
        }
        order.setSteps(workOrderStepMapper.selectList(
                new LambdaQueryWrapper<WorkOrderStep>()
                        .eq(WorkOrderStep::getWorkOrderId, id)
                        .orderByAsc(WorkOrderStep::getStepNo)));
        fillNames(order);
        return order;
    }

    @Override
    @Transactional
    public void createOrder(WorkOrderDTO dto) {
        long count = count(new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getOrderNo, dto.getOrderNo()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NO_EXISTS, "工单编号已存在");
        }
        WorkOrder order = new WorkOrder();
        BeanUtils.copyProperties(dto, order);
        order.setStatus(0);

        ProcessRoute route = processRouteMapper.selectById(dto.getRouteId());
        if (route != null) {
            order.setRouteName(route.getRouteName());
        }

        save(order);

        List<ProcessStep> steps = processStepMapper.selectList(
                new LambdaQueryWrapper<ProcessStep>()
                        .eq(ProcessStep::getRouteId, dto.getRouteId())
                        .orderByAsc(ProcessStep::getStepNo));
        for (ProcessStep step : steps) {
            WorkOrderStep ws = new WorkOrderStep();
            ws.setWorkOrderId(order.getId());
            ws.setStepNo(step.getStepNo());
            ws.setStepName(step.getStepName());
            ws.setStandardTime(step.getStandardTime());
            ws.setEquipmentType(step.getEquipmentType());
            ws.setDescription(step.getDescription());
            workOrderStepMapper.insert(ws);
        }
    }

    @Override
    public void updateOrder(Long id, WorkOrderDTO dto) {
        WorkOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND, "工单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR, "只有已创建状态可以编辑");
        }
        BeanUtils.copyProperties(dto, order);
        updateById(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        WorkOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND, "工单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR, "只有已创建状态可以删除");
        }
        workOrderStepMapper.delete(new LambdaQueryWrapper<WorkOrderStep>()
                .eq(WorkOrderStep::getWorkOrderId, id));
        removeById(id);
    }

    @Override
    public void release(Long id) {
        WorkOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND, "工单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR, "只有已创建状态可以下达");
        }
        order.setStatus(1);
        updateById(order);
    }

    @Override
    public void start(Long id) {
        WorkOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND, "工单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR, "只有已下达状态可以开工");
        }
        order.setStatus(2);
        updateById(order);
    }

    @Override
    @Transactional
    public void complete(Long id, BigDecimal actualQty) {
        WorkOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND, "工单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR, "只有生产中状态可以完工");
        }

        // 物料扣减：按实际产量 × BOM用量
        deductMaterials(order.getProductId(), actualQty);

        // 成品自动入库
        stockInFinishedProduct(order.getProductId(), actualQty, order.getOrderNo());

        order.setStatus(3);
        order.setActualQty(actualQty);
        updateById(order);

        // 自动创建 OQC 成品检验单（跨模块调用）
        createOqcInspection(order.getId(), order.getProductId(), actualQty.intValue());
    }

    @Override
    public void close(Long id) {
        WorkOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND, "工单不存在");
        }
        if (order.getStatus() != 3) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR, "只有已完工状态可以关闭");
        }
        order.setStatus(4);
        updateById(order);
    }

    /**
     * 物料扣减：按实际产量 × BOM用量 × (1 + 损耗率) 扣减库存
     */
    private void deductMaterials(Long productId, BigDecimal actualQty) {
        List<BomItem> bomItems = bomItemMapper.selectList(
                new LambdaQueryWrapper<BomItem>()
                        .eq(BomItem::getProductId, productId));
        if (bomItems.isEmpty()) return;

        // 通过ApplicationContext获取库存服务（跨模块调用）
        try {
            Object stockService = applicationContext.getBean("stockServiceImpl");
            Object inventoryService = applicationContext.getBean("inventoryServiceImpl");
            Object warehouseService = applicationContext.getBean("warehouseServiceImpl");

            // 获取默认仓库
            Object defaultWarehouse = getDefaultWarehouse(warehouseService);
            if (defaultWarehouse == null) return;
            Long warehouseId = (Long) defaultWarehouse.getClass().getMethod("getId").invoke(defaultWarehouse);

            for (BomItem item : bomItems) {
                // 扣减数量 = 实际产量 × 单位用量 × (1 + 损耗率/100)
                BigDecimal wasteMultiplier = BigDecimal.ONE;
                if (item.getWasteRate() != null && item.getWasteRate().compareTo(BigDecimal.ZERO) > 0) {
                    wasteMultiplier = BigDecimal.ONE.add(item.getWasteRate().divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP));
                }
                BigDecimal deductQty = actualQty.multiply(item.getQuantity()).multiply(wasteMultiplier);

                // 检查库存
                java.lang.reflect.Method getByProductAndWarehouse = inventoryService.getClass()
                        .getMethod("getByProductAndWarehouse", Long.class, Long.class);
                Object inventory = getByProductAndWarehouse.invoke(inventoryService, item.getMaterialId(), warehouseId);
                if (inventory == null) {
                    throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT, "物料库存不足: " + item.getMaterialId());
                }
                BigDecimal onHand = (BigDecimal) inventory.getClass().getMethod("getOnHandQty").invoke(inventory);
                if (onHand.compareTo(deductQty) < 0) {
                    Product material = productMapper.selectById(item.getMaterialId());
                    String name = material != null ? material.getProductName() : String.valueOf(item.getMaterialId());
                    throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT, "物料库存不足: " + name);
                }

                // 执行出库
                java.lang.reflect.Method stockOut = stockService.getClass()
                        .getMethod("stockOut", Class.forName("com.erp.inventory.dto.StockOutDTO"));
                Object stockOutDTO = Class.forName("com.erp.inventory.dto.StockOutDTO").getDeclaredConstructor().newInstance();
                stockOutDTO.getClass().getMethod("setProductId", Long.class).invoke(stockOutDTO, item.getMaterialId());
                stockOutDTO.getClass().getMethod("setWarehouseId", Long.class).invoke(stockOutDTO, warehouseId);
                stockOutDTO.getClass().getMethod("setQuantity", BigDecimal.class).invoke(stockOutDTO, deductQty);
                stockOutDTO.getClass().getMethod("setReferenceNo", String.class).invoke(stockOutDTO, "WO-" + productId);
                stockOutDTO.getClass().getMethod("setRemark", String.class).invoke(stockOutDTO, "工单物料扣减");
                stockOut.invoke(stockService, stockOutDTO);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ignored) {
            // 库存模块未部署时跳过
        }
    }

    /**
     * 成品自动入库
     */
    private void stockInFinishedProduct(Long productId, BigDecimal actualQty, String orderNo) {
        try {
            Object stockService = applicationContext.getBean("stockServiceImpl");
            Object warehouseService = applicationContext.getBean("warehouseServiceImpl");

            Object defaultWarehouse = getDefaultWarehouse(warehouseService);
            if (defaultWarehouse == null) return;
            Long warehouseId = (Long) defaultWarehouse.getClass().getMethod("getId").invoke(defaultWarehouse);

            java.lang.reflect.Method stockIn = stockService.getClass()
                    .getMethod("stockIn", Class.forName("com.erp.inventory.dto.StockInDTO"));
            Object stockInDTO = Class.forName("com.erp.inventory.dto.StockInDTO").getDeclaredConstructor().newInstance();
            stockInDTO.getClass().getMethod("setProductId", Long.class).invoke(stockInDTO, productId);
            stockInDTO.getClass().getMethod("setWarehouseId", Long.class).invoke(stockInDTO, warehouseId);
            stockInDTO.getClass().getMethod("setQuantity", BigDecimal.class).invoke(stockInDTO, actualQty);
            stockInDTO.getClass().getMethod("setReferenceNo", String.class).invoke(stockInDTO, orderNo);
            stockInDTO.getClass().getMethod("setReferenceType", String.class).invoke(stockInDTO, "WORK_ORDER");
            stockInDTO.getClass().getMethod("setRemark", String.class).invoke(stockInDTO, "工单完工自动入库");
            stockIn.invoke(stockService, stockInDTO);
        } catch (Exception ignored) {
            // 库存模块未部署时跳过
        }
    }

    private Object getDefaultWarehouse(Object warehouseService) throws Exception {
        java.lang.reflect.Method listMethod = warehouseService.getClass()
                .getMethod("list", com.baomidou.mybatisplus.core.conditions.Wrapper.class);
        @SuppressWarnings("unchecked")
        List<?> warehouses = (List<?>) listMethod.invoke(warehouseService,
                new LambdaQueryWrapper<>()
                        .eq(com.erp.inventory.entity.Warehouse::getStatus, 1)
                        .last("limit 1"));
        return warehouses != null && !warehouses.isEmpty() ? warehouses.get(0) : null;
    }

    private void fillNames(WorkOrder order) {
        if (order.getProductId() != null) {
            Product p = productMapper.selectById(order.getProductId());
            if (p != null) order.setProductName(p.getProductName());
        }
        if (order.getWorkshopId() != null) {
            Workshop w = workshopMapper.selectById(order.getWorkshopId());
            if (w != null) order.setWorkshopName(w.getWorkshopName());
        }
    }

    /**
     * 工单完工时自动创建 OQC 成品检验单（跨模块调用）
     */
    private void createOqcInspection(Long workOrderId, Long productId, int quantity) {
        try {
            Object oqInspectionService = applicationContext.getBean("oqInspectionServiceImpl");
            java.lang.reflect.Method createMethod = oqInspectionService.getClass()
                    .getMethod("createForWorkOrder", Long.class, Long.class, Integer.class);
            createMethod.invoke(oqInspectionService, workOrderId, productId, quantity);
        } catch (Exception ignored) {
            // 质量模块未部署时跳过
        }
    }
}
