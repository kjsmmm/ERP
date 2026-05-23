package com.erp.purchase.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.purchase.dto.PurchaseOrderDTO;
import com.erp.purchase.entity.PurchaseOrder;

public interface PurchaseOrderService extends IService<PurchaseOrder> {
    IPage<PurchaseOrder> getPurchaseOrderPage(String keyword, Integer status, Integer pageNum, Integer pageSize);
    void createPurchaseOrder(PurchaseOrderDTO dto);
    void confirmOrder(Long id);
    void cancelOrder(Long id);
}
