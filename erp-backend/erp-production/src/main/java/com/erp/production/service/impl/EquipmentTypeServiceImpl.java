package com.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.production.dto.EquipmentTypeDTO;
import com.erp.production.entity.Equipment;
import com.erp.production.entity.EquipmentType;
import com.erp.production.entity.ProcessStep;
import com.erp.production.mapper.EquipmentMapper;
import com.erp.production.mapper.EquipmentTypeMapper;
import com.erp.production.mapper.ProcessStepMapper;
import com.erp.production.service.EquipmentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentTypeServiceImpl extends ServiceImpl<EquipmentTypeMapper, EquipmentType> implements EquipmentTypeService {

    private final EquipmentMapper equipmentMapper;
    private final ProcessStepMapper processStepMapper;

    @Override
    public List<EquipmentType> listAll() {
        return list(new LambdaQueryWrapper<EquipmentType>().orderByAsc(EquipmentType::getTypeCode));
    }

    @Override
    public void createEquipmentType(EquipmentTypeDTO dto) {
        long count = count(new LambdaQueryWrapper<EquipmentType>()
                .eq(EquipmentType::getTypeCode, dto.getTypeCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.EQUIPMENT_TYPE_CODE_EXISTS, "设备类型编码已存在");
        }
        EquipmentType type = new EquipmentType();
        BeanUtils.copyProperties(dto, type);
        save(type);
    }

    @Override
    public void updateEquipmentType(Long id, EquipmentTypeDTO dto) {
        EquipmentType type = getById(id);
        if (type == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "设备类型不存在");
        }
        BeanUtils.copyProperties(dto, type);
        updateById(type);
    }

    @Override
    public void deleteEquipmentType(Long id) {
        long eqCount = equipmentMapper.selectCount(
                new LambdaQueryWrapper<Equipment>().eq(Equipment::getEquipmentTypeId, id));
        if (eqCount > 0) {
            throw new BusinessException(ErrorCode.EQUIPMENT_TYPE_IN_USE, "设备类型已被引用，不能删除");
        }
        removeById(id);
    }
}
