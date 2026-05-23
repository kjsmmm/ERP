package com.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ProcessRouteLookup;
import com.erp.production.dto.ProcessRouteDTO;
import com.erp.production.dto.ProcessStepDTO;
import com.erp.production.entity.ProcessRoute;
import com.erp.production.entity.ProcessStep;
import com.erp.production.mapper.ProcessRouteMapper;
import com.erp.production.mapper.ProcessStepMapper;
import com.erp.production.service.ProcessRouteService;
import com.erp.product.entity.Product;
import com.erp.product.mapper.ProductMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessRouteServiceImpl extends ServiceImpl<ProcessRouteMapper, ProcessRoute> implements ProcessRouteService, ProcessRouteLookup {

    private final ProcessStepMapper processStepMapper;
    private final ProductMapper productMapper;

    @Override
    public List<ProcessRoute> getByProductId(Long productId) {
        List<ProcessRoute> routes = list(new LambdaQueryWrapper<ProcessRoute>()
                .eq(ProcessRoute::getProductId, productId)
                .orderByDesc(ProcessRoute::getIsDefault)
                .orderByAsc(ProcessRoute::getRouteCode));
        fillProductName(routes);
        return routes;
    }

    @Override
    public ProcessRoute getDefaultByProductId(Long productId) {
        ProcessRoute route = getOne(new LambdaQueryWrapper<ProcessRoute>()
                .eq(ProcessRoute::getProductId, productId)
                .eq(ProcessRoute::getIsDefault, 1)
                .last("limit 1"));
        if (route != null) {
            route.setSteps(processStepMapper.selectList(
                    new LambdaQueryWrapper<ProcessStep>()
                            .eq(ProcessStep::getRouteId, route.getId())
                            .orderByAsc(ProcessStep::getStepNo)));
        }
        return route;
    }

    @Override
    public ProcessRoute getDetail(Long id) {
        ProcessRoute route = getById(id);
        if (route == null) {
            throw new BusinessException(ErrorCode.PROCESS_ROUTE_NOT_FOUND, "工艺路线不存在");
        }
        route.setSteps(processStepMapper.selectList(
                new LambdaQueryWrapper<ProcessStep>()
                        .eq(ProcessStep::getRouteId, id)
                        .orderByAsc(ProcessStep::getStepNo)));
        Product product = productMapper.selectById(route.getProductId());
        if (product != null) {
            route.setProductName(product.getProductName());
        }
        return route;
    }

    @Override
    @Transactional
    public void createRoute(ProcessRouteDTO dto) {
        long count = count(new LambdaQueryWrapper<ProcessRoute>()
                .eq(ProcessRoute::getProductId, dto.getProductId())
                .eq(ProcessRoute::getRouteCode, dto.getRouteCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PROCESS_ROUTE_NOT_FOUND, "同一产品下路线编码已存在");
        }
        ProcessRoute route = new ProcessRoute();
        BeanUtils.copyProperties(dto, route);
        route.setVersion(1);
        route.setStatus(1);
        if (route.getIsDefault() == null) {
            route.setIsDefault(0);
        }
        if (route.getIsDefault() == 1) {
            unsetDefault(route.getProductId());
        }
        save(route);
        saveSteps(route.getId(), dto.getSteps());
    }

    @Override
    @Transactional
    public void updateRoute(Long id, ProcessRouteDTO dto) {
        ProcessRoute route = getById(id);
        if (route == null) {
            throw new BusinessException(ErrorCode.PROCESS_ROUTE_NOT_FOUND, "工艺路线不存在");
        }
        BeanUtils.copyProperties(dto, route);
        if (route.getIsDefault() == 1) {
            unsetDefault(route.getProductId());
        }
        updateById(route);
        processStepMapper.delete(new LambdaQueryWrapper<ProcessStep>()
                .eq(ProcessStep::getRouteId, id));
        saveSteps(id, dto.getSteps());
    }

    @Override
    @Transactional
    public void deleteRoute(Long id) {
        processStepMapper.delete(new LambdaQueryWrapper<ProcessStep>()
                .eq(ProcessStep::getRouteId, id));
        removeById(id);
    }

    @Override
    @Transactional
    public void setDefault(Long id) {
        ProcessRoute route = getById(id);
        if (route == null) {
            throw new BusinessException(ErrorCode.PROCESS_ROUTE_NOT_FOUND, "工艺路线不存在");
        }
        unsetDefault(route.getProductId());
        route.setIsDefault(1);
        updateById(route);
    }

    private void unsetDefault(Long productId) {
        ProcessRoute existing = getOne(new LambdaQueryWrapper<ProcessRoute>()
                .eq(ProcessRoute::getProductId, productId)
                .eq(ProcessRoute::getIsDefault, 1)
                .last("limit 1"));
        if (existing != null) {
            existing.setIsDefault(0);
            updateById(existing);
        }
    }

    private void saveSteps(Long routeId, List<ProcessStepDTO> steps) {
        if (steps == null || steps.isEmpty()) return;
        for (ProcessStepDTO stepDTO : steps) {
            ProcessStep step = new ProcessStep();
            BeanUtils.copyProperties(stepDTO, step);
            step.setRouteId(routeId);
            processStepMapper.insert(step);
        }
    }

    private void fillProductName(List<ProcessRoute> routes) {
        if (routes == null || routes.isEmpty()) return;
        for (ProcessRoute route : routes) {
            if (route.getProductId() != null) {
                Product product = productMapper.selectById(route.getProductId());
                if (product != null) {
                    route.setProductName(product.getProductName());
                }
            }
        }
    }

    @Override
    public RouteSummary getDefaultRouteByProductId(Long productId) {
        ProcessRoute route = getDefaultByProductId(productId);
        if (route == null) return null;
        RouteSummaryImpl summary = new RouteSummaryImpl();
        summary.setId(route.getId());
        summary.setRouteCode(route.getRouteCode());
        summary.setRouteName(route.getRouteName());
        summary.setVersion(route.getVersion());
        if (route.getSteps() != null) {
            List<StepSummaryImpl> stepList = new ArrayList<>();
            for (ProcessStep step : route.getSteps()) {
                StepSummaryImpl ss = new StepSummaryImpl();
                ss.setStepNo(step.getStepNo());
                ss.setStepName(step.getStepName());
                ss.setStandardTime(step.getStandardTime());
                ss.setEquipmentType(step.getEquipmentType());
                ss.setDescription(step.getDescription());
                stepList.add(ss);
            }
            summary.setSteps(stepList);
        }
        return summary;
    }

    @Data
    public static class RouteSummaryImpl implements RouteSummary {
        private Long id;
        private String routeCode;
        private String routeName;
        private Integer version;
        private List<StepSummaryImpl> steps;
    }

    @Data
    public static class StepSummaryImpl implements StepSummary {
        private Integer stepNo;
        private String stepName;
        private BigDecimal standardTime;
        private String equipmentType;
        private String description;
    }
}
