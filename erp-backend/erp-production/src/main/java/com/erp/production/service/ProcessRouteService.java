package com.erp.production.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.production.dto.ProcessRouteDTO;
import com.erp.production.entity.ProcessRoute;

import java.util.List;

public interface ProcessRouteService extends IService<ProcessRoute> {
    List<ProcessRoute> getByProductId(Long productId);
    ProcessRoute getDefaultByProductId(Long productId);
    ProcessRoute getDetail(Long id);
    void createRoute(ProcessRouteDTO dto);
    void updateRoute(Long id, ProcessRouteDTO dto);
    void deleteRoute(Long id);
    void setDefault(Long id);
}
