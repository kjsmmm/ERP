package com.erp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.production.dto.TeamDTO;
import com.erp.production.entity.Team;

import java.util.List;

public interface TeamService extends IService<Team> {
    IPage<Team> getTeamPage(Long workshopId, Integer pageNum, Integer pageSize);
    List<Team> getByWorkshopId(Long workshopId);
    void createTeam(TeamDTO dto);
    void updateTeam(Long id, TeamDTO dto);
    void deleteTeam(Long id);
}
