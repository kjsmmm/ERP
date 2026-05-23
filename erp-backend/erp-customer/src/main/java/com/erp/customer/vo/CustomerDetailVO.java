package com.erp.customer.vo;

import com.erp.customer.entity.Customer;
import com.erp.customer.entity.CustomerContact;
import com.erp.customer.entity.CustomerFollow;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 客户详情VO（聚合客户信息+联系人+跟进记录）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerDetailVO extends Customer {

    /**
     * 联系人列表
     */
    private List<CustomerContact> contacts;

    /**
     * 最近跟进记录
     */
    private List<CustomerFollow> recentFollows;
}
