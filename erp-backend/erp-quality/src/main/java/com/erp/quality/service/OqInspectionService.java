package com.erp.quality.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.quality.dto.OqInspectionDTO;
import com.erp.quality.entity.OqInspection;

public interface OqInspectionService extends IService<OqInspection> {
    IPage<OqInspection> getOqInspectionPage(String keyword, Integer inspectionResult, Integer pageNum, Integer pageSize);
    void createInspection(OqInspectionDTO dto);
    void submitResult(Long id, OqInspectionDTO dto);

    /**
     * 工单完成时自动创建 OQC 检验单
     */
    void createForWorkOrder(Long workOrderId, Long productId, Integer quantity);
}
