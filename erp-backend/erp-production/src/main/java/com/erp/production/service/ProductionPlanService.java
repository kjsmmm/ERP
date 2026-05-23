package com.erp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.production.dto.ProductionPlanDTO;
import com.erp.production.entity.ProductionPlan;

public interface ProductionPlanService extends IService<ProductionPlan> {
    IPage<ProductionPlan> getPlanPage(Integer status, Integer pageNum, Integer pageSize);
    void createPlan(ProductionPlanDTO dto);
    void updatePlan(Long id, ProductionPlanDTO dto);
    void deletePlan(Long id);
    void release(Long id);
    void start(Long id);
    void complete(Long id);
}
