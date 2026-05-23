package com.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.production.dto.WorkReportDTO;
import com.erp.production.entity.WorkOrder;
import com.erp.production.entity.WorkReport;
import com.erp.production.mapper.WorkOrderMapper;
import com.erp.production.mapper.WorkReportMapper;
import com.erp.production.service.WorkReportService;
import com.erp.system.entity.SysUser;
import com.erp.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkReportServiceImpl extends ServiceImpl<WorkReportMapper, WorkReport> implements WorkReportService {

    private final WorkOrderMapper workOrderMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public void createReport(WorkReportDTO dto) {
        WorkOrder order = workOrderMapper.selectById(dto.getWorkOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND, "工单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR, "只有生产中的工单可以报工");
        }

        WorkReport report = new WorkReport();
        BeanUtils.copyProperties(dto, report);
        report.setReportTime(LocalDateTime.now());
        save(report);

        // 累加工单实际数量
        order.setActualQty(order.getActualQty() == null
                ? dto.getReportQty()
                : order.getActualQty().add(dto.getReportQty()));
        workOrderMapper.updateById(order);
    }

    @Override
    public List<WorkReport> getByOrderId(Long workOrderId) {
        List<WorkReport> reports = list(new LambdaQueryWrapper<WorkReport>()
                .eq(WorkReport::getWorkOrderId, workOrderId)
                .orderByAsc(WorkReport::getStepNo)
                .orderByDesc(WorkReport::getReportTime));
        reports.forEach(this::fillReporterName);
        return reports;
    }

    private void fillReporterName(WorkReport report) {
        if (report.getReporterId() != null) {
            SysUser user = sysUserMapper.selectById(report.getReporterId());
            if (user != null) report.setReporterName(user.getRealName());
        }
    }
}
