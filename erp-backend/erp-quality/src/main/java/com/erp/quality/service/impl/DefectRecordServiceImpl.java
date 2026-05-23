package com.erp.quality.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.workflow.WorkflowService;
import com.erp.quality.dto.DefectRecordDTO;
import com.erp.quality.entity.DefectRecord;
import com.erp.quality.mapper.DefectRecordMapper;
import com.erp.quality.service.DefectRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DefectRecordServiceImpl extends ServiceImpl<DefectRecordMapper, DefectRecord> implements DefectRecordService {

    private final WorkflowService workflowService;

    @Override
    public IPage<DefectRecord> getDefectRecordPage(String keyword, Integer handleType, Integer status, Integer pageNum, Integer pageSize) {
        Page<DefectRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DefectRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(DefectRecord::getRecordNo, keyword);
        }
        if (handleType != null) {
            wrapper.eq(DefectRecord::getHandleType, handleType);
        }
        if (status != null) {
            wrapper.eq(DefectRecord::getStatus, status);
        }
        wrapper.orderByDesc(DefectRecord::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void createRecord(DefectRecordDTO dto) {
        DefectRecord record = new DefectRecord();
        BeanUtils.copyProperties(dto, record);
        record.setRecordNo(generateRecordNo());
        record.setStatus(0);
        save(record);
    }

    @Override
    @Transactional
    public void submitForApproval(Long id) {
        DefectRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.DEFECT_RECORD_NOT_FOUND, "不合格品记录不存在");
        }
        if (record.getStatus() != 0) {
            throw new BusinessException(ErrorCode.DEFECT_RECORD_STATUS_ERROR, "只有待审批状态可以提交");
        }

        String processInstanceId = workflowService.startProcess(
                "defect-handling-approval",
                String.valueOf(id),
                String.valueOf(getCurrentUserId()),
                null
        );

        record.setProcessInstanceId(processInstanceId);
        record.setStatus(1);
        updateById(record);
    }

    @Override
    @Transactional
    public void approveCallback(String processInstanceId, boolean approved) {
        DefectRecord record = getOne(new LambdaQueryWrapper<DefectRecord>()
                .eq(DefectRecord::getProcessInstanceId, processInstanceId));
        if (record == null) {
            return;
        }
        record.setStatus(approved ? 2 : 3);
        updateById(record);
    }

    private String generateRecordNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "DEF-" + dateStr + "-";
        DefectRecord maxRecord = getOne(new LambdaQueryWrapper<DefectRecord>()
                .likeRight(DefectRecord::getRecordNo, prefix)
                .orderByDesc(DefectRecord::getRecordNo)
                .last("LIMIT 1"));
        int seq = 1;
        if (maxRecord != null && maxRecord.getRecordNo() != null && maxRecord.getRecordNo().startsWith(prefix)) {
            try {
                seq = Integer.parseInt(maxRecord.getRecordNo().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("DEF-%s-%03d", dateStr, seq);
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
