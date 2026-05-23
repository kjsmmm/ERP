package com.erp.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.customer.entity.CustomerContact;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户联系人 Mapper
 */
@Mapper
public interface ContactMapper extends BaseMapper<CustomerContact> {
}
