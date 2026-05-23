package com.erp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.inventory.dto.WarehouseDTO;
import com.erp.inventory.entity.Warehouse;
import com.erp.inventory.mapper.WarehouseMapper;
import com.erp.inventory.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWarehouse(WarehouseDTO dto) {
        long count = count(new LambdaQueryWrapper<Warehouse>()
                .eq(Warehouse::getWarehouseCode, dto.getWarehouseCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "仓库编码已存在");
        }
        Warehouse warehouse = new Warehouse();
        BeanUtils.copyProperties(dto, warehouse);
        warehouse.setStatus(1);
        save(warehouse);
        return warehouse.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWarehouse(Long id, WarehouseDTO dto) {
        Warehouse warehouse = getById(id);
        if (warehouse == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "仓库不存在");
        }
        long count = count(new LambdaQueryWrapper<Warehouse>()
                .eq(Warehouse::getWarehouseCode, dto.getWarehouseCode())
                .ne(Warehouse::getId, id));
        if (count > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "仓库编码已存在");
        }
        BeanUtils.copyProperties(dto, warehouse);
        warehouse.setId(id);
        updateById(warehouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = getById(id);
        if (warehouse == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "仓库不存在");
        }
        removeById(id);
    }

    @Override
    public List<Warehouse> listActive() {
        return list(new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getStatus, 1));
    }
}
