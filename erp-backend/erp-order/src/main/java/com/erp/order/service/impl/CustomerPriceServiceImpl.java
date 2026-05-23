package com.erp.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.order.entity.CustomerProductPrice;
import com.erp.order.mapper.CustomerPriceMapper;
import com.erp.order.service.CustomerPriceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerPriceServiceImpl extends ServiceImpl<CustomerPriceMapper, CustomerProductPrice>
        implements CustomerPriceService {

    @Override
    public List<CustomerProductPrice> getByCustomerId(Long customerId) {
        return list(new LambdaQueryWrapper<CustomerProductPrice>()
                .eq(CustomerProductPrice::getCustomerId, customerId));
    }

    @Override
    public BigDecimal getPrice(Long customerId, Long productId) {
        CustomerProductPrice record = getOne(new LambdaQueryWrapper<CustomerProductPrice>()
                .eq(CustomerProductPrice::getCustomerId, customerId)
                .eq(CustomerProductPrice::getProductId, productId));
        return record != null ? record.getPrice() : null;
    }

    @Override
    public void saveOrUpdatePrice(Long customerId, Long productId, BigDecimal price, String remark) {
        CustomerProductPrice existing = getOne(new LambdaQueryWrapper<CustomerProductPrice>()
                .eq(CustomerProductPrice::getCustomerId, customerId)
                .eq(CustomerProductPrice::getProductId, productId));
        if (existing != null) {
            existing.setPrice(price);
            existing.setRemark(remark);
            updateById(existing);
        } else {
            CustomerProductPrice record = new CustomerProductPrice();
            record.setCustomerId(customerId);
            record.setProductId(productId);
            record.setPrice(price);
            record.setRemark(remark);
            save(record);
        }
    }

    @Override
    public void deletePrice(Long id) {
        removeById(id);
    }
}
