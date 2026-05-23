package com.erp.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.customer.dto.FollowDTO;
import com.erp.customer.entity.CustomerFollow;

/**
 * 客户跟进记录服务接口
 */
public interface FollowService extends IService<CustomerFollow> {

    /**
     * 分页查询客户的跟进记录
     *
     * @param customerId 客户ID
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 分页结果
     */
    IPage<CustomerFollow> getFollowPage(Long customerId, Integer pageNum, Integer pageSize);

    /**
     * 创建跟进记录
     *
     * @param dto 跟进记录信息
     * @return 跟进记录ID
     */
    Long createFollow(FollowDTO dto);

    /**
     * 删除跟进记录
     *
     * @param id 跟进记录ID
     */
    void deleteFollow(Long id);
}
