package com.erp.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.order.dto.SalesReturnDTO;
import com.erp.order.entity.SalesReturn;

public interface SalesReturnService extends IService<SalesReturn> {
    IPage<SalesReturn> getReturnPage(String keyword, Integer status, Integer pageNum, Integer pageSize);
    void createReturn(SalesReturnDTO dto);
    void submitForApproval(Long id);
    void approveCallback(String processInstanceId, boolean approved);
    void receive(Long id);
}
