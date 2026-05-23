package com.erp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.production.dto.EquipmentDTO;
import com.erp.production.entity.Equipment;

public interface EquipmentService extends IService<Equipment> {
    IPage<Equipment> getEquipmentPage(Long workshopId, Long equipmentTypeId, Integer status, Integer pageNum, Integer pageSize);
    void createEquipment(EquipmentDTO dto);
    void updateEquipment(Long id, EquipmentDTO dto);
    void deleteEquipment(Long id);
}
