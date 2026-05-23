package com.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.system.dto.LogQueryDTO;
import com.erp.system.entity.SysLog;
import com.erp.system.mapper.SysLogMapper;
import com.erp.system.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 操作日志服务实现
 */
@Slf4j
@Service
public class LogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements LogService {

    @Override
    public IPage<SysLog> getLogPage(LogQueryDTO queryDTO) {
        Page<SysLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getModule()), SysLog::getModule, queryDTO.getModule());
        wrapper.like(StringUtils.hasText(queryDTO.getOperation()), SysLog::getOperation, queryDTO.getOperation());
        wrapper.eq(queryDTO.getOperatorId() != null, SysLog::getOperatorId, queryDTO.getOperatorId());
        wrapper.eq(queryDTO.getStatus() != null, SysLog::getStatus, queryDTO.getStatus());
        wrapper.ge(queryDTO.getStartTime() != null, SysLog::getCreatedAt, queryDTO.getStartTime());
        wrapper.le(queryDTO.getEndTime() != null, SysLog::getCreatedAt, queryDTO.getEndTime());
        wrapper.orderByDesc(SysLog::getCreatedAt);

        return page(page, wrapper);
    }

    @Override
    public void saveLog(SysLog sysLog) {
        try {
            save(sysLog);
        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage(), e);
        }
    }
}
