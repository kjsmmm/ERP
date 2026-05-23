package com.erp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.production.dto.WorkOrderDTO;
import com.erp.production.entity.WorkOrder;

import java.math.BigDecimal;

public interface WorkOrderService extends IService<WorkOrder> {
    IPage<WorkOrder> getOrderPage(Long workshopId, Integer status, Integer pageNum, Integer pageSize);
    WorkOrder getDetail(Long id);
    void createOrder(WorkOrderDTO dto);
    void updateOrder(Long id, WorkOrderDTO dto);
    void deleteOrder(Long id);
    void release(Long id);
    void start(Long id);
    void complete(Long id, BigDecimal actualQty);
    void close(Long id);
}
