package com.erp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.system.entity.SysLog;
import com.erp.system.dto.LogQueryDTO;

/**
 * 操作日志服务接口
 */
public interface LogService extends IService<SysLog> {

    /**
     * 分页查询操作日志
     *
     * @param queryDTO 查询条件
     * @return 日志分页
     */
    IPage<SysLog> getLogPage(LogQueryDTO queryDTO);

    /**
     * 记录操作日志
     *
     * @param log 操作日志
     */
    void saveLog(SysLog log);
}
