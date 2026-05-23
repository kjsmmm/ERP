package com.erp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.production.dto.WorkshopDTO;
import com.erp.production.entity.Workshop;

public interface WorkshopService extends IService<Workshop> {
    IPage<Workshop> getWorkshopPage(String keyword, Integer pageNum, Integer pageSize);
    void createWorkshop(WorkshopDTO dto);
    void updateWorkshop(Long id, WorkshopDTO dto);
    void deleteWorkshop(Long id);
}
