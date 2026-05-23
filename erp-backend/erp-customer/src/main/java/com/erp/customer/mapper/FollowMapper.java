package com.erp.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.customer.entity.CustomerFollow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户跟进记录 Mapper
 */
@Mapper
public interface FollowMapper extends BaseMapper<CustomerFollow> {
}
