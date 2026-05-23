package com.erp.product.vo;

import com.erp.product.entity.BomItem;
import com.erp.product.entity.Product;
import com.erp.product.entity.ProductImage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品详情VO（聚合产品信息+图片+BOM+工艺路线）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductDetailVO extends Product {

    private String categoryName;

    private List<ProductImage> images;

    private List<BomItem> bomItems;

    /**
     * 默认工艺路线摘要
     */
    private ProcessRouteSummary defaultProcessRoute;

    @Data
    public static class ProcessRouteSummary {
        private Long id;
        private String routeCode;
        private String routeName;
        private Integer version;
        private List<StepSummary> steps;
    }

    @Data
    public static class StepSummary {
        private Integer stepNo;
        private String stepName;
        private BigDecimal standardTime;
        private String equipmentType;
        private String description;
    }
}
