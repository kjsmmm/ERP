package com.erp.purchase.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.purchase.dto.PurchaseRequestDTO;
import com.erp.purchase.entity.PurchaseRequest;

public interface PurchaseRequestService extends IService<PurchaseRequest> {
    IPage<PurchaseRequest> getPurchaseRequestPage(String keyword, Integer status, Integer pageNum, Integer pageSize);
    void createPurchaseRequest(PurchaseRequestDTO dto);
    void submitForApproval(Long id);
    void approveCallback(String processInstanceId, boolean approved);
}
