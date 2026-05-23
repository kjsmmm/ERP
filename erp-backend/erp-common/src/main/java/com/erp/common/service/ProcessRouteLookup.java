package com.erp.common.service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 工艺路线查询接口（跨模块使用，避免循环依赖）
 */
public interface ProcessRouteLookup {

    RouteSummary getDefaultRouteByProductId(Long productId);

    interface RouteSummary {
        Long getId();
        String getRouteCode();
        String getRouteName();
        Integer getVersion();
        List<? extends StepSummary> getSteps();
    }

    interface StepSummary {
        Integer getStepNo();
        String getStepName();
        BigDecimal getStandardTime();
        String getEquipmentType();
        String getDescription();
    }
}
