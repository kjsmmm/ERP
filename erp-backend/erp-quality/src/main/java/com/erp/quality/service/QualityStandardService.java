package com.erp.quality.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.quality.dto.QualityStandardDTO;
import com.erp.quality.entity.QualityStandard;

public interface QualityStandardService extends IService<QualityStandard> {
    IPage<QualityStandard> getStandardPage(String keyword, Integer applicableType, Integer pageNum, Integer pageSize);
    void createStandard(QualityStandardDTO dto);
    void updateStandard(Long id, QualityStandardDTO dto);
    void deleteStandard(Long id);
}
