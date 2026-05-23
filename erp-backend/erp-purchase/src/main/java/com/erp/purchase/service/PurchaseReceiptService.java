package com.erp.purchase.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.purchase.dto.PurchaseReceiptDTO;
import com.erp.purchase.entity.PurchaseReceipt;

public interface PurchaseReceiptService extends IService<PurchaseReceipt> {
    IPage<PurchaseReceipt> getPurchaseReceiptPage(String keyword, Integer pageNum, Integer pageSize);
    void createReceipt(PurchaseReceiptDTO dto);
}
