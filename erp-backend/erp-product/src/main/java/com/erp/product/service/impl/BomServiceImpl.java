package com.erp.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.product.dto.BomItemDTO;
import com.erp.product.entity.BomItem;
import com.erp.product.entity.Product;
import com.erp.product.mapper.BomItemMapper;
import com.erp.product.mapper.ProductMapper;
import com.erp.product.service.BomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * BOM服务实现
 */
@Service
@RequiredArgsConstructor
public class BomServiceImpl extends ServiceImpl<BomItemMapper, BomItem> implements BomService {

    private final ProductMapper productMapper;

    @Override
    public List<BomItem> getBomByProductId(Long productId) {
        List<BomItem> items = list(new LambdaQueryWrapper<BomItem>()
                .eq(BomItem::getProductId, productId)
                .orderByAsc(BomItem::getSortOrder));

        // 填充物料信息
        for (BomItem item : items) {
            Product material = productMapper.selectById(item.getMaterialId());
            if (material != null) {
                item.setMaterialName(material.getProductName());
                item.setMaterialCode(material.getProductCode());
                item.setMaterialUnit(material.getUnit());
                item.setMaterialSpec(material.getSpec());
                item.setMaterialType(material.getProductType());
            }
        }
        return items;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBom(Long productId, List<BomItemDTO> items) {
        // 校验产品类型
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "产品不存在");
        }
        if (product.getProductType() == 1) {
            throw new BusinessException(ErrorCode.RAW_MATERIAL_NO_BOM, "原材料不能添加BOM");
        }

        // 环检测：先检查新提交批次内部的循环引用，再检查与已有BOM的循环
        if (items != null && !items.isEmpty()) {
            Set<Long> batchIds = new HashSet<>();
            for (BomItemDTO item : items) {
                if (!batchIds.add(item.getMaterialId())) {
                    throw new BusinessException(ErrorCode.BOM_CIRCULAR_REFERENCE,
                            "BOM子项中存在重复物料ID: " + item.getMaterialId());
                }
            }
            Set<Long> visited = new HashSet<>();
            visited.add(productId);
            for (BomItemDTO item : items) {
                checkCircularReference(item.getMaterialId(), visited);
            }
        }

        // 删除旧的BOM子项
        remove(new LambdaQueryWrapper<BomItem>().eq(BomItem::getProductId, productId));

        // 插入新的BOM子项
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                BomItemDTO dto = items.get(i);
                BomItem item = new BomItem();
                item.setProductId(productId);
                item.setMaterialId(dto.getMaterialId());
                item.setQuantity(dto.getQuantity());
                item.setWasteRate(dto.getWasteRate() != null ? dto.getWasteRate() : java.math.BigDecimal.ZERO);
                item.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : i);
                save(item);
            }
        }
    }

    @Override
    public List<BomItem> expandBomTree(Long productId) {
        return baseMapper.expandBomTree(productId);
    }

    /**
     * 检测BOM环引用
     */
    private void checkCircularReference(Long materialId, Set<Long> visited) {
        if (visited.contains(materialId)) {
            throw new BusinessException(ErrorCode.BOM_CIRCULAR_REFERENCE, "BOM存在循环引用，物料ID: " + materialId);
        }
        visited.add(materialId);

        // 检查该物料的子项
        List<BomItem> children = list(new LambdaQueryWrapper<BomItem>()
                .eq(BomItem::getProductId, materialId));
        for (BomItem child : children) {
            checkCircularReference(child.getMaterialId(), new HashSet<>(visited));
        }
    }
}
