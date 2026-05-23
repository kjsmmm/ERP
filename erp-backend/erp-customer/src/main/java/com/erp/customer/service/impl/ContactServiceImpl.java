package com.erp.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.customer.dto.ContactDTO;
import com.erp.customer.entity.CustomerContact;
import com.erp.customer.mapper.ContactMapper;
import com.erp.customer.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 客户联系人服务实现
 */
@Service
@RequiredArgsConstructor
public class ContactServiceImpl extends ServiceImpl<ContactMapper, CustomerContact> implements ContactService {

    @Override
    public List<CustomerContact> getContactsByCustomerId(Long customerId) {
        return list(new LambdaQueryWrapper<CustomerContact>()
                .eq(CustomerContact::getCustomerId, customerId)
                .orderByDesc(CustomerContact::getIsPrimary)
                .orderByAsc(CustomerContact::getCreatedAt));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createContact(ContactDTO dto) {
        // 如果设置为主要联系人，先清除该客户的其他主要联系人
        if (dto.getIsPrimary() != null && dto.getIsPrimary() == 1) {
            clearPrimaryContact(dto.getCustomerId());
        }

        CustomerContact contact = new CustomerContact();
        BeanUtils.copyProperties(dto, contact);
        save(contact);
        return contact.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContact(Long id, ContactDTO dto) {
        CustomerContact contact = getById(id);
        if (contact == null) {
            throw new RuntimeException("联系人不存在");
        }

        // 如果设置为主要联系人，先清除该客户的其他主要联系人
        if (dto.getIsPrimary() != null && dto.getIsPrimary() == 1) {
            clearPrimaryContact(dto.getCustomerId());
        }

        BeanUtils.copyProperties(dto, contact);
        contact.setId(id);
        updateById(contact);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContact(Long id) {
        CustomerContact contact = getById(id);
        if (contact == null) {
            throw new RuntimeException("联系人不存在");
        }
        removeById(id);
    }

    /**
     * 清除客户的主要联系人标记
     */
    private void clearPrimaryContact(Long customerId) {
        CustomerContact update = new CustomerContact();
        update.setIsPrimary(0);
        update(update, new LambdaQueryWrapper<CustomerContact>()
                .eq(CustomerContact::getCustomerId, customerId)
                .eq(CustomerContact::getIsPrimary, 1));
    }
}
