package com.erp.quality.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.production.entity.WorkOrder;
import com.erp.production.mapper.WorkOrderMapper;
import com.erp.quality.dto.OqInspectionDTO;
import com.erp.quality.entity.OqInspection;
import com.erp.quality.entity.OqInspectionItem;
import com.erp.quality.mapper.OqInspectionItemMapper;
import com.erp.quality.mapper.OqInspectionMapper;
import com.erp.quality.service.OqInspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OqInspectionServiceImpl extends ServiceImpl<OqInspectionMapper, OqInspection> implements OqInspectionService {

    private final OqInspectionItemMapper inspectionItemMapper;
    private final WorkOrderMapper workOrderMapper;

    @Override
    public IPage<OqInspection> getOqInspectionPage(String keyword, Integer inspectionResult, Integer pageNum, Integer pageSize) {
        Page<OqInspection> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OqInspection> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(OqInspection::getInspectionNo, keyword);
        }
        if (inspectionResult != null) {
            wrapper.eq(OqInspection::getInspectionResult, inspectionResult);
        }
        wrapper.orderByDesc(OqInspection::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void createInspection(OqInspectionDTO dto) {
        WorkOrder workOrder = workOrderMapper.selectById(dto.getWorkOrderId());
        if (workOrder == null) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND, "工单不存在");
        }

        OqInspection inspection = new OqInspection();
        inspection.setInspectionNo(generateInspectionNo());
        inspection.setWorkOrderId(dto.getWorkOrderId());
        inspection.setProductId(workOrder.getProductId());
        inspection.setQuantity(workOrder.getActualQty());
        inspection.setInspectionResult(0);
        inspection.setStatus(0);
        inspection.setRemark(dto.getRemark());
        save(inspection);

        if (dto.getItems() != null) {
            for (OqInspectionDTO.OqInspectionItemDTO itemDTO : dto.getItems()) {
                OqInspectionItem item = new OqInspectionItem();
                BeanUtils.copyProperties(itemDTO, item);
                item.setOqInspectionId(inspection.getId());
                inspectionItemMapper.insert(item);
            }
        }
    }

    @Override
    @Transactional
    public void submitResult(Long id, OqInspectionDTO dto) {
        OqInspection inspection = getById(id);
        if (inspection == null) {
            throw new BusinessException(ErrorCode.OQ_INSPECTION_NOT_FOUND, "成品检验单不存在");
        }
        if (inspection.getStatus() == 2) {
            throw new BusinessException(ErrorCode.INSPECTION_NOT_COMPLETED, "检验已完成，不能修改");
        }

        inspectionItemMapper.delete(new LambdaQueryWrapper<OqInspectionItem>()
                .eq(OqInspectionItem::getOqInspectionId, id));

        boolean hasDefect = false;
        if (dto.getItems() != null) {
            for (OqInspectionDTO.OqInspectionItemDTO itemDTO : dto.getItems()) {
                OqInspectionItem item = new OqInspectionItem();
                BeanUtils.copyProperties(itemDTO, item);
                item.setOqInspectionId(id);
                inspectionItemMapper.insert(item);
                if (itemDTO.getJudgment() != null && itemDTO.getJudgment() == 2) {
                    hasDefect = true;
                }
            }
        }

        inspection.setStatus(2);
        inspection.setInspectionResult(hasDefect ? 2 : 1);
        inspection.setInspectorId(1L);
        inspection.setInspectionTime(LocalDateTime.now());
        updateById(inspection);
    }

    @Override
    @Transactional
    public void createForWorkOrder(Long workOrderId, Long productId, Integer quantity) {
        OqInspection inspection = new OqInspection();
        inspection.setInspectionNo(generateInspectionNo());
        inspection.setWorkOrderId(workOrderId);
        inspection.setProductId(productId);
        inspection.setQuantity(quantity);
        inspection.setInspectionResult(0);
        inspection.setStatus(0);
        save(inspection);
    }

    private String generateInspectionNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "OQC-" + dateStr + "-";
        OqInspection maxInspection = getOne(new LambdaQueryWrapper<OqInspection>()
                .likeRight(OqInspection::getInspectionNo, prefix)
                .orderByDesc(OqInspection::getInspectionNo)
                .last("LIMIT 1"));
        int seq = 1;
        if (maxInspection != null && maxInspection.getInspectionNo() != null && maxInspection.getInspectionNo().startsWith(prefix)) {
            try {
                seq = Integer.parseInt(maxInspection.getInspectionNo().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("OQC-%s-%03d", dateStr, seq);
    }
}
