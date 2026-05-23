package com.erp.production.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.production.dto.WorkReportDTO;
import com.erp.production.entity.WorkReport;

import java.util.List;

public interface WorkReportService extends IService<WorkReport> {
    void createReport(WorkReportDTO dto);
    List<WorkReport> getByOrderId(Long workOrderId);
}
