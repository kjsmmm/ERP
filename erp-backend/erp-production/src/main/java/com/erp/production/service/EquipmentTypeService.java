package com.erp.production.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.production.dto.EquipmentTypeDTO;
import com.erp.production.entity.EquipmentType;

import java.util.List;

public interface EquipmentTypeService extends IService<EquipmentType> {
    List<EquipmentType> listAll();
    void createEquipmentType(EquipmentTypeDTO dto);
    void updateEquipmentType(Long id, EquipmentTypeDTO dto);
    void deleteEquipmentType(Long id);
}
