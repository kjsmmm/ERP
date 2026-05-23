package com.erp.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.customer.dto.CustomerDTO;
import com.erp.customer.dto.CustomerQueryDTO;
import com.erp.customer.entity.Customer;
import com.erp.customer.vo.CustomerDetailVO;

/**
 * 客户服务接口
 */
public interface CustomerService extends IService<Customer> {

    /**
     * 分页查询客户
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<Customer> getCustomerPage(CustomerQueryDTO queryDTO);

    /**
     * 获取客户详情（含联系人+跟进记录）
     *
     * @param id 客户ID
     * @return 客户详情
     */
    CustomerDetailVO getCustomerDetail(Long id);

    /**
     * 创建客户
     *
     * @param dto 客户信息
     * @return 客户ID
     */
    Long createCustomer(CustomerDTO dto);

    /**
     * 更新客户
     *
     * @param id  客户ID
     * @param dto 客户信息
     */
    void updateCustomer(Long id, CustomerDTO dto);

    /**
     * 删除客户
     *
     * @param id 客户ID
     */
    void deleteCustomer(Long id);

    /**
     * 修改客户状态
     *
     * @param id     客户ID
     * @param status 状态
     */
    void changeStatus(Long id, Integer status);
}
