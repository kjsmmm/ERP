package com.erp.production.controller;

import com.erp.common.result.Result;
import com.erp.production.dto.EquipmentTypeDTO;
import com.erp.production.entity.EquipmentType;
import com.erp.production.service.EquipmentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "设备类型管理")
@RestController
@RequestMapping("/equipment-types")
@RequiredArgsConstructor
public class EquipmentTypeController {

    private final EquipmentTypeService equipmentTypeService;

    @Operation(summary = "设备类型列表")
    @GetMapping
    public Result<List<EquipmentType>> list() {
        return Result.success(equipmentTypeService.listAll());
    }

    @Operation(summary = "创建设备类型")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody EquipmentTypeDTO dto) {
        equipmentTypeService.createEquipmentType(dto);
        return Result.success();
    }

    @Operation(summary = "更新设备类型")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody EquipmentTypeDTO dto) {
        equipmentTypeService.updateEquipmentType(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除设备类型")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        equipmentTypeService.deleteEquipmentType(id);
        return Result.success();
    }
}
