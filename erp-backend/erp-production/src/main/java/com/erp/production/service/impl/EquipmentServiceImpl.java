package com.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.production.dto.EquipmentDTO;
import com.erp.production.entity.Equipment;
import com.erp.production.entity.EquipmentType;
import com.erp.production.entity.Workshop;
import com.erp.production.mapper.EquipmentMapper;
import com.erp.production.mapper.EquipmentTypeMapper;
import com.erp.production.mapper.WorkshopMapper;
import com.erp.production.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    private final EquipmentTypeMapper equipmentTypeMapper;
    private final WorkshopMapper workshopMapper;

    @Override
    public IPage<Equipment> getEquipmentPage(Long workshopId, Long equipmentTypeId, Integer status,
                                              Integer pageNum, Integer pageSize) {
        Page<Equipment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(workshopId != null, Equipment::getWorkshopId, workshopId);
        wrapper.eq(equipmentTypeId != null, Equipment::getEquipmentTypeId, equipmentTypeId);
        wrapper.eq(status != null, Equipment::getStatus, status);
        wrapper.orderByDesc(Equipment::getCreatedAt);
        IPage<Equipment> result = page(page, wrapper);
        result.getRecords().forEach(this::fillNames);
        return result;
    }

    @Override
    public void createEquipment(EquipmentDTO dto) {
        long count = count(new LambdaQueryWrapper<Equipment>()
                .eq(Equipment::getEquipmentCode, dto.getEquipmentCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.EQUIPMENT_CODE_EXISTS, "设备编码已存在");
        }
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(dto, equipment);
        equipment.setStatus(1);
        save(equipment);
    }

    @Override
    public void updateEquipment(Long id, EquipmentDTO dto) {
        Equipment equipment = getById(id);
        if (equipment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "设备不存在");
        }
        BeanUtils.copyProperties(dto, equipment);
        updateById(equipment);
    }

    @Override
    public void deleteEquipment(Long id) {
        removeById(id);
    }

    private void fillNames(Equipment eq) {
        if (eq.getEquipmentTypeId() != null) {
            EquipmentType t = equipmentTypeMapper.selectById(eq.getEquipmentTypeId());
            if (t != null) eq.setEquipmentTypeName(t.getTypeName());
        }
        if (eq.getWorkshopId() != null) {
            Workshop w = workshopMapper.selectById(eq.getWorkshopId());
            if (w != null) eq.setWorkshopName(w.getWorkshopName());
        }
    }
}
