package com.erp.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.product.entity.BomItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * BOM物料清单 Mapper
 */
@Mapper
public interface BomItemMapper extends BaseMapper<BomItem> {

    /**
     * 递归展开BOM树（使用MySQL递归CTE）
     *
     * @param productId 父产品ID
     * @return 展开后的BOM项列表（含level层级信息）
     */
    @Select("""
        WITH RECURSIVE bom_tree AS (
            SELECT bi.id, bi.product_id, bi.material_id, bi.quantity, bi.waste_rate, bi.sort_order,
                   p.product_name AS material_name, p.product_code AS material_code,
                   p.unit AS material_unit, p.spec AS material_spec, p.product_type AS material_type,
                   1 AS level
            FROM bom_item bi
            JOIN product p ON bi.material_id = p.id AND p.deleted = 0
            WHERE bi.product_id = #{productId} AND bi.deleted = 0

            UNION ALL

            SELECT bi.id, bi.product_id, bi.material_id, bi.quantity, bi.waste_rate, bi.sort_order,
                   p.product_name AS material_name, p.product_code AS material_code,
                   p.unit AS material_unit, p.spec AS material_spec, p.product_type AS material_type,
                   bt.level + 1
            FROM bom_item bi
            JOIN bom_tree bt ON bi.product_id = bt.material_id
            JOIN product p ON bi.material_id = p.id AND p.deleted = 0
            WHERE bi.deleted = 0 AND bt.level < 10
        )
        SELECT * FROM bom_tree ORDER BY level, sort_order
    """)
    List<BomItem> expandBomTree(@Param("productId") Long productId);
}
