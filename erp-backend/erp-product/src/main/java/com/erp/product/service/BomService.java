package com.erp.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.product.dto.BomItemDTO;
import com.erp.product.entity.BomItem;

import java.util.List;

/**
 * BOM服务接口
 */
public interface BomService extends IService<BomItem> {

    /**
     * 获取产品的直接BOM子项
     *
     * @param productId 产品ID
     * @return BOM子项列表
     */
    List<BomItem> getBomByProductId(Long productId);

    /**
     * 更新产品BOM（整体替换）
     *
     * @param productId 产品ID
     * @param items     BOM子项列表
     */
    void updateBom(Long productId, List<BomItemDTO> items);

    /**
     * 递归展开BOM树
     *
     * @param productId 产品ID
     * @return 展开后的BOM项列表
     */
    List<BomItem> expandBomTree(Long productId);
}
