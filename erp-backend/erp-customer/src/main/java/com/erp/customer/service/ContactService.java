package com.erp.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.customer.dto.ContactDTO;
import com.erp.customer.entity.CustomerContact;

import java.util.List;

/**
 * 客户联系人服务接口
 */
public interface ContactService extends IService<CustomerContact> {

    /**
     * 获取客户的所有联系人
     *
     * @param customerId 客户ID
     * @return 联系人列表
     */
    List<CustomerContact> getContactsByCustomerId(Long customerId);

    /**
     * 创建联系人
     *
     * @param dto 联系人信息
     * @return 联系人ID
     */
    Long createContact(ContactDTO dto);

    /**
     * 更新联系人
     *
     * @param id  联系人ID
     * @param dto 联系人信息
     */
    void updateContact(Long id, ContactDTO dto);

    /**
     * 删除联系人
     *
     * @param id 联系人ID
     */
    void deleteContact(Long id);
}
