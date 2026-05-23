package com.erp.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.inventory.dto.WarehouseDTO;
import com.erp.inventory.entity.Warehouse;

import java.util.List;

public interface WarehouseService extends IService<Warehouse> {
    Long createWarehouse(WarehouseDTO dto);
    void updateWarehouse(Long id, WarehouseDTO dto);
    void deleteWarehouse(Long id);
    List<Warehouse> listActive();
}
