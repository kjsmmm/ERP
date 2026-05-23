package com.erp.quality.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.quality.dto.IqInspectionDTO;
import com.erp.quality.entity.IqInspection;
import com.erp.quality.entity.IqInspectionItem;
import com.erp.quality.entity.QualityStandard;
import com.erp.quality.entity.QualityStandardItem;
import com.erp.quality.mapper.IqInspectionItemMapper;
import com.erp.quality.mapper.IqInspectionMapper;
import com.erp.quality.mapper.QualityStandardItemMapper;
import com.erp.quality.mapper.QualityStandardMapper;
import com.erp.quality.service.IqInspectionService;
import com.erp.purchase.entity.PurchaseOrder;
import com.erp.purchase.mapper.PurchaseOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IqInspectionServiceImpl extends ServiceImpl<IqInspectionMapper, IqInspection> implements IqInspectionService {

    private final IqInspectionItemMapper inspectionItemMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final QualityStandardMapper standardMapper;
    private final QualityStandardItemMapper standardItemMapper;

    @Override
    public IPage<IqInspection> getIqInspectionPage(String keyword, Integer inspectionResult, Integer pageNum, Integer pageSize) {
        Page<IqInspection> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<IqInspection> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(IqInspection::getInspectionNo, keyword);
        }
        if (inspectionResult != null) {
            wrapper.eq(IqInspection::getInspectionResult, inspectionResult);
        }
        wrapper.orderByDesc(IqInspection::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void createInspection(IqInspectionDTO dto) {
        PurchaseOrder order = purchaseOrderMapper.selectById(dto.getPurchaseOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "采购单不存在");
        }

        IqInspection inspection = new IqInspection();
        inspection.setInspectionNo(generateInspectionNo());
        inspection.setPurchaseOrderId(dto.getPurchaseOrderId());
        inspection.setSupplierId(order.getSupplierId());
        inspection.setInspectionResult(0);
        inspection.setStatus(0);
        inspection.setRemark(dto.getRemark());
        save(inspection);

        if (dto.getItems() != null) {
            for (IqInspectionDTO.IqInspectionItemDTO itemDTO : dto.getItems()) {
                IqInspectionItem item = new IqInspectionItem();
                BeanUtils.copyProperties(itemDTO, item);
                item.setIqInspectionId(inspection.getId());
                inspectionItemMapper.insert(item);
            }
        }
    }

    @Override
    @Transactional
    public void submitResult(Long id, IqInspectionDTO dto) {
        IqInspection inspection = getById(id);
        if (inspection == null) {
            throw new BusinessException(ErrorCode.IQ_INSPECTION_NOT_FOUND, "来料检验单不存在");
        }
        if (inspection.getStatus() == 2) {
            throw new BusinessException(ErrorCode.INSPECTION_NOT_COMPLETED, "检验已完成，不能修改");
        }

        inspectionItemMapper.delete(new LambdaQueryWrapper<IqInspectionItem>()
                .eq(IqInspectionItem::getIqInspectionId, id));

        boolean hasDefect = false;
        if (dto.getItems() != null) {
            for (IqInspectionDTO.IqInspectionItemDTO itemDTO : dto.getItems()) {
                IqInspectionItem item = new IqInspectionItem();
                BeanUtils.copyProperties(itemDTO, item);
                item.setIqInspectionId(id);
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
    public boolean isIqcPassed(Long purchaseOrderId) {
        IqInspection inspection = getOne(new LambdaQueryWrapper<IqInspection>()
                .eq(IqInspection::getPurchaseOrderId, purchaseOrderId)
                .last("LIMIT 1"));
        if (inspection == null) {
            return false;
        }
        return inspection.getStatus() == 2 && inspection.getInspectionResult() == 1;
    }

    private String generateInspectionNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "IQC-" + dateStr + "-";
        IqInspection maxInspection = getOne(new LambdaQueryWrapper<IqInspection>()
                .likeRight(IqInspection::getInspectionNo, prefix)
                .orderByDesc(IqInspection::getInspectionNo)
                .last("LIMIT 1"));
        int seq = 1;
        if (maxInspection != null && maxInspection.getInspectionNo() != null && maxInspection.getInspectionNo().startsWith(prefix)) {
            try {
                seq = Integer.parseInt(maxInspection.getInspectionNo().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("IQC-%s-%03d", dateStr, seq);
    }
}
