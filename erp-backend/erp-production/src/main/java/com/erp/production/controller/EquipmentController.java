package com.erp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.result.PageResult;
import com.erp.common.result.Result;
import com.erp.production.dto.EquipmentDTO;
import com.erp.production.entity.Equipment;
import com.erp.production.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "设备管理")
@RestController
@RequestMapping("/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Operation(summary = "设备分页查询")
    @GetMapping
    public Result<PageResult<Equipment>> page(
            @RequestParam(required = false) Long workshopId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Equipment> page = equipmentService.getEquipmentPage(workshopId, equipmentTypeId, status, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "创建设备")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody EquipmentDTO dto) {
        equipmentService.createEquipment(dto);
        return Result.success();
    }

    @Operation(summary = "更新设备")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody EquipmentDTO dto) {
        equipmentService.updateEquipment(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return Result.success();
    }
}
