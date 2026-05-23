package com.erp.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.order.entity.CustomerProductPrice;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerPriceService extends IService<CustomerProductPrice> {

    /**
     * 获取客户的产品专属价格列表
     */
    List<CustomerProductPrice> getByCustomerId(Long customerId);

    /**
     * 获取客户某产品的专属价格，无记录返回null
     */
    BigDecimal getPrice(Long customerId, Long productId);

    /**
     * 保存或更新客户产品价格
     */
    void saveOrUpdatePrice(Long customerId, Long productId, BigDecimal price, String remark);

    /**
     * 删除客户产品价格
     */
    void deletePrice(Long id);
}
