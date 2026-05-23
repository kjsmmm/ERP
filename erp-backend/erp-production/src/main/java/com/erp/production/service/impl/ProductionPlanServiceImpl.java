package com.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.production.dto.ProductionPlanDTO;
import com.erp.production.entity.ProductionPlan;
import com.erp.production.mapper.ProductionPlanMapper;
import com.erp.production.service.ProductionPlanService;
import com.erp.product.entity.Product;
import com.erp.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductionPlanServiceImpl extends ServiceImpl<ProductionPlanMapper, ProductionPlan> implements ProductionPlanService {

    private final ProductMapper productMapper;

    @Override
    public IPage<ProductionPlan> getPlanPage(Integer status, Integer pageNum, Integer pageSize) {
        Page<ProductionPlan> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ProductionPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, ProductionPlan::getStatus, status);
        wrapper.orderByDesc(ProductionPlan::getCreatedAt);
        IPage<ProductionPlan> result = page(page, wrapper);
        result.getRecords().forEach(this::fillNames);
        return result;
    }

    @Override
    public void createPlan(ProductionPlanDTO dto) {
        long count = count(new LambdaQueryWrapper<ProductionPlan>()
                .eq(ProductionPlan::getPlanCode, dto.getPlanCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PLAN_CODE_EXISTS, "计划编码已存在");
        }
        ProductionPlan plan = new ProductionPlan();
        BeanUtils.copyProperties(dto, plan);
        plan.setStatus(0);
        save(plan);
    }

    @Override
    public void updatePlan(Long id, ProductionPlanDTO dto) {
        ProductionPlan plan = getById(id);
        if (plan == null) {
            throw new BusinessException(ErrorCode.PLAN_NOT_FOUND, "生产计划不存在");
        }
        BeanUtils.copyProperties(dto, plan);
        updateById(plan);
    }

    @Override
    public void deletePlan(Long id) {
        ProductionPlan plan = getById(id);
        if (plan == null) {
            throw new BusinessException(ErrorCode.PLAN_NOT_FOUND, "生产计划不存在");
        }
        if (plan.getStatus() != 0) {
            throw new BusinessException(ErrorCode.PLAN_CANNOT_DELETE, "非草稿状态不能删除");
        }
        removeById(id);
    }

    @Override
    public void release(Long id) {
        ProductionPlan plan = getById(id);
        if (plan == null) {
            throw new BusinessException(ErrorCode.PLAN_NOT_FOUND, "生产计划不存在");
        }
        if (plan.getStatus() != 0) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR, "只有草稿状态可以下达");
        }
        plan.setStatus(1);
        updateById(plan);
    }

    @Override
    public void start(Long id) {
        ProductionPlan plan = getById(id);
        if (plan == null) {
            throw new BusinessException(ErrorCode.PLAN_NOT_FOUND, "生产计划不存在");
        }
        if (plan.getStatus() != 1) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR, "只有已下达状态可以开始执行");
        }
        plan.setStatus(2);
        updateById(plan);
    }

    @Override
    public void complete(Long id) {
        ProductionPlan plan = getById(id);
        if (plan == null) {
            throw new BusinessException(ErrorCode.PLAN_NOT_FOUND, "生产计划不存在");
        }
        if (plan.getStatus() != 2) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR, "只有执行中状态可以完成");
        }
        plan.setStatus(3);
        updateById(plan);
    }

    private void fillNames(ProductionPlan plan) {
        if (plan.getProductId() != null) {
            Product p = productMapper.selectById(plan.getProductId());
            if (p != null) plan.setProductName(p.getProductName());
        }
    }
}
