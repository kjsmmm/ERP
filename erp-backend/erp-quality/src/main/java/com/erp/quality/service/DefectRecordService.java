package com.erp.quality.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.quality.dto.DefectRecordDTO;
import com.erp.quality.entity.DefectRecord;

public interface DefectRecordService extends IService<DefectRecord> {
    IPage<DefectRecord> getDefectRecordPage(String keyword, Integer handleType, Integer status, Integer pageNum, Integer pageSize);
    void createRecord(DefectRecordDTO dto);
    void submitForApproval(Long id);
    void approveCallback(String processInstanceId, boolean approved);
}
