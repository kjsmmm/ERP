package com.erp.quality.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.quality.dto.QualityStandardDTO;
import com.erp.quality.entity.QualityStandard;
import com.erp.quality.entity.QualityStandardItem;
import com.erp.quality.mapper.QualityStandardItemMapper;
import com.erp.quality.mapper.QualityStandardMapper;
import com.erp.quality.service.QualityStandardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class QualityStandardServiceImpl extends ServiceImpl<QualityStandardMapper, QualityStandard> implements QualityStandardService {

    private final QualityStandardItemMapper standardItemMapper;

    @Override
    public IPage<QualityStandard> getStandardPage(String keyword, Integer applicableType, Integer pageNum, Integer pageSize) {
        Page<QualityStandard> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<QualityStandard> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(QualityStandard::getStandardCode, keyword)
                    .or()
                    .like(QualityStandard::getStandardName, keyword);
        }
        if (applicableType != null) {
            wrapper.eq(QualityStandard::getApplicableType, applicableType);
        }
        wrapper.orderByDesc(QualityStandard::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void createStandard(QualityStandardDTO dto) {
        long count = count(new LambdaQueryWrapper<QualityStandard>()
                .eq(QualityStandard::getStandardCode, dto.getStandardCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.STANDARD_CODE_EXISTS, "检验标准编码已存在");
        }

        QualityStandard standard = new QualityStandard();
        BeanUtils.copyProperties(dto, standard);
        standard.setStatus(1);
        save(standard);

        for (QualityStandardDTO.QualityStandardItemDTO itemDTO : dto.getItems()) {
            QualityStandardItem item = new QualityStandardItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setStandardId(standard.getId());
            standardItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void updateStandard(Long id, QualityStandardDTO dto) {
        QualityStandard standard = getById(id);
        if (standard == null) {
            throw new BusinessException(ErrorCode.STANDARD_NOT_FOUND, "检验标准不存在");
        }

        if (!standard.getStandardCode().equals(dto.getStandardCode())) {
            long count = count(new LambdaQueryWrapper<QualityStandard>()
                    .eq(QualityStandard::getStandardCode, dto.getStandardCode()));
            if (count > 0) {
                throw new BusinessException(ErrorCode.STANDARD_CODE_EXISTS, "检验标准编码已存在");
            }
        }

        BeanUtils.copyProperties(dto, standard);
        updateById(standard);

        standardItemMapper.delete(new LambdaQueryWrapper<QualityStandardItem>()
                .eq(QualityStandardItem::getStandardId, id));
        for (QualityStandardDTO.QualityStandardItemDTO itemDTO : dto.getItems()) {
            QualityStandardItem item = new QualityStandardItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setStandardId(id);
            standardItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void deleteStandard(Long id) {
        QualityStandard standard = getById(id);
        if (standard == null) {
            throw new BusinessException(ErrorCode.STANDARD_NOT_FOUND, "检验标准不存在");
        }
        standardItemMapper.delete(new LambdaQueryWrapper<QualityStandardItem>()
                .eq(QualityStandardItem::getStandardId, id));
        removeById(id);
    }
}
