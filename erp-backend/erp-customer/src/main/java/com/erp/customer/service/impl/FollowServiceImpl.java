package com.erp.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.context.UserIdProvider;
import com.erp.customer.dto.FollowDTO;
import com.erp.customer.entity.CustomerFollow;
import com.erp.customer.mapper.FollowMapper;
import com.erp.customer.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户跟进记录服务实现
 */
@Service
@RequiredArgsConstructor
public class FollowServiceImpl extends ServiceImpl<FollowMapper, CustomerFollow> implements FollowService {

    private final UserIdProvider userIdProvider;

    @Override
    public IPage<CustomerFollow> getFollowPage(Long customerId, Integer pageNum, Integer pageSize) {
        Page<CustomerFollow> page = new Page<>(pageNum, pageSize);

        return page(page, new LambdaQueryWrapper<CustomerFollow>()
                .eq(CustomerFollow::getCustomerId, customerId)
                .orderByDesc(CustomerFollow::getFollowTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFollow(FollowDTO dto) {
        CustomerFollow follow = new CustomerFollow();
        BeanUtils.copyProperties(dto, follow);

        // 自动设置跟进人为当前登录用户
        Long currentUserId = userIdProvider.getCurrentUserId();
        if (currentUserId != null) {
            follow.setOperatorId(currentUserId);
        }

        save(follow);
        return follow.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFollow(Long id) {
        CustomerFollow follow = getById(id);
        if (follow == null) {
            throw new RuntimeException("跟进记录不存在");
        }
        removeById(id);
    }
}
