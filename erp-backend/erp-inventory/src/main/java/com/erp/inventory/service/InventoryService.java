package com.erp.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.inventory.dto.InventoryQueryDTO;
import com.erp.inventory.entity.Inventory;

import java.math.BigDecimal;
import java.util.List;

public interface InventoryService extends IService<Inventory> {

    IPage<Inventory> getInventoryPage(InventoryQueryDTO queryDTO);

    List<Inventory> getByProductId(Long productId);

    Inventory getByProductAndWarehouse(Long productId, Long warehouseId);

    /**
     * 预留库存（增加 reserved_qty）
     */
    void reserve(Long productId, Long warehouseId, BigDecimal quantity);

    /**
     * 释放预留（减少 reserved_qty）
     */
    void release(Long productId, Long warehouseId, BigDecimal quantity);

    /**
     * 库存预警查询（on_hand_qty < safety_stock 且 safety_stock > 0）
     */
    List<Inventory> getAlertList();
}
