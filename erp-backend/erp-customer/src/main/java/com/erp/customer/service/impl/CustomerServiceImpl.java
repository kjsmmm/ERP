package com.erp.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.context.UserIdProvider;
import com.erp.customer.dto.CustomerDTO;
import com.erp.customer.dto.CustomerQueryDTO;
import com.erp.customer.entity.Customer;
import com.erp.customer.entity.CustomerContact;
import com.erp.customer.entity.CustomerFollow;
import com.erp.customer.mapper.CustomerMapper;
import com.erp.customer.service.ContactService;
import com.erp.customer.service.CustomerService;
import com.erp.customer.service.FollowService;
import com.erp.customer.vo.CustomerDetailVO;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 客户服务实现
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    private final ContactService contactService;
    private final FollowService followService;
    private final UserIdProvider userIdProvider;
    private final CustomerMapper customerMapper;

    @Override
    public IPage<Customer> getCustomerPage(CustomerQueryDTO queryDTO) {
        Page<Customer> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.hasText(queryDTO.getKeyword()), w ->
                w.like(Customer::getCustomerName, queryDTO.getKeyword())
                 .or()
                 .like(Customer::getCustomerCode, queryDTO.getKeyword()));
        wrapper.eq(queryDTO.getCustomerType() != null, Customer::getCustomerType, queryDTO.getCustomerType());
        wrapper.eq(queryDTO.getCustomerLevel() != null, Customer::getCustomerLevel, queryDTO.getCustomerLevel());
        wrapper.like(StringUtils.hasText(queryDTO.getIndustry()), Customer::getIndustry, queryDTO.getIndustry());
        wrapper.eq(queryDTO.getStatus() != null, Customer::getStatus, queryDTO.getStatus());
        wrapper.orderByDesc(Customer::getCreatedAt);

        return page(page, wrapper);
    }

    @Override
    public CustomerDetailVO getCustomerDetail(Long id) {
        Customer customer = getById(id);
        if (customer == null) {
            return null;
        }

        CustomerDetailVO vo = new CustomerDetailVO();
        BeanUtils.copyProperties(customer, vo);

        // 加载联系人
        List<CustomerContact> contacts = contactService.getContactsByCustomerId(id);
        vo.setContacts(contacts);

        // 加载最近10条跟进记录
        IPage<CustomerFollow> followPage = followService.getFollowPage(id, 1, 10);
        vo.setRecentFollows(followPage.getRecords());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCustomer(CustomerDTO dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);

        // 自动生成客户编码：CUS-YYYYMMDD-NNN
        customer.setCustomerCode(generateCustomerCode());
        customer.setStatus(1);

        save(customer);
        return customer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND, "客户不存在");
        }

        BeanUtils.copyProperties(dto, customer);
        customer.setId(id);
        updateById(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomer(Long id) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND, "客户不存在");
        }

        // 检查是否有关联联系人
        long contactCount = contactService.count(new LambdaQueryWrapper<CustomerContact>()
                .eq(CustomerContact::getCustomerId, id));
        if (contactCount > 0) {
            throw new BusinessException(ErrorCode.CUSTOMER_HAS_CONTACTS, "该客户下有联系人，不能删除");
        }

        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, Integer status) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND, "客户不存在");
        }
        customer.setStatus(status);
        updateById(customer);
    }

    /**
     * 生成客户编码：CUS-YYYYMMDD-NNN（查询当日最大序号+1）
     */
    private String generateCustomerCode() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "CUS-" + dateStr + "-";

        // 查询当日最大编码
        String maxCode = customerMapper.selectOne(new LambdaQueryWrapper<Customer>()
                .likeRight(Customer::getCustomerCode, prefix)
                .orderByDesc(Customer::getCustomerCode)
                .last("LIMIT 1")
        ).getCustomerCode();

        int seq = 1;
        if (maxCode != null && maxCode.startsWith(prefix)) {
            try {
                seq = Integer.parseInt(maxCode.substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }

        return String.format("CUS-%s-%03d", dateStr, seq);
    }
}
