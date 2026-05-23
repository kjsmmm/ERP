package com.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.production.dto.TeamDTO;
import com.erp.production.entity.Team;
import com.erp.production.entity.Workshop;
import com.erp.production.mapper.TeamMapper;
import com.erp.production.mapper.WorkshopMapper;
import com.erp.production.service.TeamService;
import com.erp.system.entity.SysUser;
import com.erp.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    private final WorkshopMapper workshopMapper;
    private final SysUserMapper userMapper;

    @Override
    public IPage<Team> getTeamPage(Long workshopId, Integer pageNum, Integer pageSize) {
        Page<Team> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(workshopId != null, Team::getWorkshopId, workshopId);
        wrapper.orderByDesc(Team::getCreatedAt);
        IPage<Team> result = page(page, wrapper);
        result.getRecords().forEach(this::fillNames);
        return result;
    }

    @Override
    public List<Team> getByWorkshopId(Long workshopId) {
        List<Team> list = list(new LambdaQueryWrapper<Team>()
                .eq(Team::getWorkshopId, workshopId));
        list.forEach(this::fillNames);
        return list;
    }

    @Override
    public void createTeam(TeamDTO dto) {
        long count = count(new LambdaQueryWrapper<Team>()
                .eq(Team::getTeamCode, dto.getTeamCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.TEAM_CODE_EXISTS, "班组编码已存在");
        }
        Workshop workshop = workshopMapper.selectById(dto.getWorkshopId());
        if (workshop == null) {
            throw new BusinessException(ErrorCode.WORKSHOP_NOT_FOUND, "车间不存在");
        }
        Team team = new Team();
        BeanUtils.copyProperties(dto, team);
        team.setStatus(1);
        save(team);
    }

    @Override
    public void updateTeam(Long id, TeamDTO dto) {
        Team team = getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班组不存在");
        }
        BeanUtils.copyProperties(dto, team);
        updateById(team);
    }

    @Override
    public void deleteTeam(Long id) {
        removeById(id);
    }

    private void fillNames(Team team) {
        if (team.getWorkshopId() != null) {
            Workshop w = workshopMapper.selectById(team.getWorkshopId());
            if (w != null) team.setWorkshopName(w.getWorkshopName());
        }
        if (team.getLeaderId() != null) {
            SysUser u = userMapper.selectById(team.getLeaderId());
            if (u != null) team.setLeaderName(u.getRealName());
        }
    }
}
