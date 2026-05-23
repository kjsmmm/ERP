package com.erp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.inventory.dto.InventoryQueryDTO;
import com.erp.inventory.entity.Inventory;
import com.erp.inventory.mapper.InventoryMapper;
import com.erp.inventory.service.InventoryService;
import com.erp.product.entity.Product;
import com.erp.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    private final ProductMapper productMapper;

    @Override
    public IPage<Inventory> getInventoryPage(InventoryQueryDTO queryDTO) {
        Page<Inventory> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getProductId() != null, Inventory::getProductId, queryDTO.getProductId());
        wrapper.eq(queryDTO.getWarehouseId() != null, Inventory::getWarehouseId, queryDTO.getWarehouseId());

        IPage<Inventory> result = page(page, wrapper);
        // 填充产品和仓库信息
        result.getRecords().forEach(this::fillProductInfo);
        return result;
    }

    @Override
    public List<Inventory> getByProductId(Long productId) {
        List<Inventory> list = list(new LambdaQueryWrapper<Inventory>().eq(Inventory::getProductId, productId));
        list.forEach(this::fillProductInfo);
        return list;
    }

    @Override
    public Inventory getByProductAndWarehouse(Long productId, Long warehouseId) {
        Inventory inv = getOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getProductId, productId)
                .eq(Inventory::getWarehouseId, warehouseId));
        if (inv != null) {
            fillProductInfo(inv);
        }
        return inv;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reserve(Long productId, Long warehouseId, BigDecimal quantity) {
        Inventory inv = getByProductAndWarehouse(productId, warehouseId);
        if (inv == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "库存记录不存在");
        }
        BigDecimal available = inv.getOnHandQty().subtract(inv.getReservedQty());
        if (available.compareTo(quantity) < 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    "可用库存不足，当前可用: " + available + "，需要: " + quantity);
        }
        inv.setReservedQty(inv.getReservedQty().add(quantity));
        updateById(inv);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void release(Long productId, Long warehouseId, BigDecimal quantity) {
        Inventory inv = getByProductAndWarehouse(productId, warehouseId);
        if (inv == null) {
            return;
        }
        BigDecimal newReserved = inv.getReservedQty().subtract(quantity);
        if (newReserved.compareTo(BigDecimal.ZERO) < 0) {
            newReserved = BigDecimal.ZERO;
        }
        inv.setReservedQty(newReserved);
        updateById(inv);
    }

    @Override
    public List<Inventory> getAlertList() {
        List<Inventory> list = list(new LambdaQueryWrapper<Inventory>()
                .gt(Inventory::getSafetyStock, BigDecimal.ZERO)
                .apply("on_hand_qty < safety_stock"));
        list.forEach(this::fillProductInfo);
        return list;
    }

    private void fillProductInfo(Inventory inv) {
        inv.setAvailableQty(inv.getOnHandQty().subtract(inv.getReservedQty()));
        if (inv.getProductId() != null) {
            Product product = productMapper.selectById(inv.getProductId());
            if (product != null) {
                inv.setProductName(product.getProductName());
                inv.setProductCode(product.getProductCode());
                inv.setUnit(product.getUnit());
            }
        }
    }
}
