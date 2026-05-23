package com.erp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.inventory.dto.StockInDTO;
import com.erp.inventory.dto.StockOutDTO;
import com.erp.inventory.entity.Inventory;
import com.erp.inventory.entity.StockRecord;
import com.erp.inventory.mapper.InventoryMapper;
import com.erp.inventory.mapper.StockRecordMapper;
import com.erp.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final InventoryMapper inventoryMapper;
    private final StockRecordMapper stockRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockIn(StockInDTO dto) {
        Inventory inv = getOrCreateInventory(dto.getProductId(), dto.getWarehouseId());
        inv.setOnHandQty(inv.getOnHandQty().add(dto.getQuantity()));
        inventoryMapper.updateById(inv);

        StockRecord record = new StockRecord();
        record.setProductId(dto.getProductId());
        record.setWarehouseId(dto.getWarehouseId());
        record.setRecordType("INBOUND");
        record.setQuantity(dto.getQuantity());
        record.setReferenceNo(dto.getReferenceNo());
        record.setReferenceType(dto.getReferenceType());
        record.setReferenceId(dto.getReferenceId());
        record.setRemark(dto.getRemark());
        stockRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockOut(StockOutDTO dto) {
        Inventory inv = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getProductId, dto.getProductId())
                .eq(Inventory::getWarehouseId, dto.getWarehouseId()));
        if (inv == null || inv.getOnHandQty().compareTo(dto.getQuantity()) < 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "库存不足");
        }

        // 出库减少实物库存，同时释放对应的预留量
        inv.setOnHandQty(inv.getOnHandQty().subtract(dto.getQuantity()));
        BigDecimal releaseQty = dto.getQuantity().min(inv.getReservedQty());
        inv.setReservedQty(inv.getReservedQty().subtract(releaseQty));
        inventoryMapper.updateById(inv);

        StockRecord record = new StockRecord();
        record.setProductId(dto.getProductId());
        record.setWarehouseId(dto.getWarehouseId());
        record.setRecordType("OUTBOUND");
        record.setQuantity(dto.getQuantity());
        record.setReferenceNo(dto.getReferenceNo());
        record.setReferenceType(dto.getReferenceType());
        record.setReferenceId(dto.getReferenceId());
        record.setRemark(dto.getRemark());
        stockRecordMapper.insert(record);
    }

    private Inventory getOrCreateInventory(Long productId, Long warehouseId) {
        Inventory inv = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getProductId, productId)
                .eq(Inventory::getWarehouseId, warehouseId));
        if (inv == null) {
            inv = new Inventory();
            inv.setProductId(productId);
            inv.setWarehouseId(warehouseId);
            inv.setOnHandQty(BigDecimal.ZERO);
            inv.setReservedQty(BigDecimal.ZERO);
            inventoryMapper.insert(inv);
        }
        return inv;
    }
}
