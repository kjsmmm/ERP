package com.erp.purchase.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.purchase.dto.SupplierDTO;
import com.erp.purchase.entity.Supplier;

import java.util.List;

public interface SupplierService extends IService<Supplier> {
    IPage<Supplier> getSupplierPage(String keyword, Integer pageNum, Integer pageSize);
    List<Supplier> listAll();
    void createSupplier(SupplierDTO dto);
    void updateSupplier(Long id, SupplierDTO dto);
    void deleteSupplier(Long id);
}
