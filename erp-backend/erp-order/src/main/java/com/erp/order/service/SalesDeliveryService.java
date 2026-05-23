package com.erp.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.order.dto.SalesDeliveryDTO;
import com.erp.order.entity.SalesDelivery;

public interface SalesDeliveryService extends IService<SalesDelivery> {
    IPage<SalesDelivery> getDeliveryPage(String keyword, Integer status, Integer pageNum, Integer pageSize);
    void createDelivery(SalesDeliveryDTO dto);
    void pick(Long id);
    void shipOut(Long id);
    void sign(Long id);
}
