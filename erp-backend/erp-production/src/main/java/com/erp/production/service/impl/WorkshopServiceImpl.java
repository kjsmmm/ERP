package com.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.production.dto.WorkshopDTO;
import com.erp.production.entity.Team;
import com.erp.production.entity.Workshop;
import com.erp.production.mapper.TeamMapper;
import com.erp.production.mapper.WorkshopMapper;
import com.erp.production.service.WorkshopService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class WorkshopServiceImpl extends ServiceImpl<WorkshopMapper, Workshop> implements WorkshopService {

    private final TeamMapper teamMapper;

    @Override
    public IPage<Workshop> getWorkshopPage(String keyword, Integer pageNum, Integer pageSize) {
        Page<Workshop> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Workshop> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Workshop::getWorkshopCode, keyword)
                    .or()
                    .like(Workshop::getWorkshopName, keyword);
        }
        wrapper.orderByDesc(Workshop::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    public void createWorkshop(WorkshopDTO dto) {
        long count = count(new LambdaQueryWrapper<Workshop>()
                .eq(Workshop::getWorkshopCode, dto.getWorkshopCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.WORKSHOP_CODE_EXISTS, "车间编码已存在");
        }
        Workshop workshop = new Workshop();
        BeanUtils.copyProperties(dto, workshop);
        workshop.setStatus(1);
        save(workshop);
    }

    @Override
    public void updateWorkshop(Long id, WorkshopDTO dto) {
        Workshop workshop = getById(id);
        if (workshop == null) {
            throw new BusinessException(ErrorCode.WORKSHOP_NOT_FOUND, "车间不存在");
        }
        BeanUtils.copyProperties(dto, workshop);
        updateById(workshop);
    }

    @Override
    public void deleteWorkshop(Long id) {
        long teamCount = teamMapper.selectCount(
                new LambdaQueryWrapper<Team>().eq(Team::getWorkshopId, id));
        if (teamCount > 0) {
            throw new BusinessException(ErrorCode.WORKSHOP_HAS_TEAMS, "车间下存在班组，不能删除");
        }
        removeById(id);
    }
}
