package com.erp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.production.dto.TeamDTO;
import com.erp.production.entity.Team;
import com.erp.production.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "班组管理")
@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "班组分页查询")
    @GetMapping
    public Result<PageResult<Team>> page(
            @RequestParam(required = false) Long workshopId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Team> page = teamService.getTeamPage(workshopId, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "按车间查询班组列表")
    @GetMapping("/workshop/{workshopId}")
    public Result<List<Team>> getByWorkshopId(@PathVariable Long workshopId) {
        return Result.success(teamService.getByWorkshopId(workshopId));
    }

    @Operation(summary = "创建班组")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody TeamDTO dto) {
        teamService.createTeam(dto);
        return Result.success();
    }

    @Operation(summary = "更新班组")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TeamDTO dto) {
        teamService.updateTeam(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除班组")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return Result.success();
    }
}
