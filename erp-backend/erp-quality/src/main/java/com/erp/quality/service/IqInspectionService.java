package com.erp.quality.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.quality.dto.IqInspectionDTO;
import com.erp.quality.entity.IqInspection;

public interface IqInspectionService extends IService<IqInspection> {
    IPage<IqInspection> getIqInspectionPage(String keyword, Integer inspectionResult, Integer pageNum, Integer pageSize);
    void createInspection(IqInspectionDTO dto);
    void submitResult(Long id, IqInspectionDTO dto);

    /**
     * 检查指定采购单的 IQC 是否已通过
     * @return true=已通过, false=无检验单或未通过
     */
    boolean isIqcPassed(Long purchaseOrderId);
}
