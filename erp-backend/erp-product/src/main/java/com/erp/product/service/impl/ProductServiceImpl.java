package com.erp.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.product.dto.ProductDTO;
import com.erp.product.dto.ProductQueryDTO;
import com.erp.product.entity.BomItem;
import com.erp.product.entity.Product;
import com.erp.product.entity.ProductCategory;
import com.erp.product.entity.ProductImage;
import com.erp.product.mapper.BomItemMapper;
import com.erp.product.mapper.ProductCategoryMapper;
import com.erp.product.mapper.ProductMapper;
import com.erp.product.service.ProductService;
import com.erp.product.vo.ProductDetailVO;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ProcessRouteLookup;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 产品服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductCategoryMapper categoryMapper;
    private final ProductImageService productImageService;
    private final BomItemMapper bomItemMapper;
    @Lazy
    private final ProcessRouteLookup processRouteLookup;

    @Override
    public IPage<Product> getProductPage(ProductQueryDTO queryDTO) {
        Page<Product> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.hasText(queryDTO.getKeyword()), w ->
                w.like(Product::getProductName, queryDTO.getKeyword())
                 .or()
                 .like(Product::getProductCode, queryDTO.getKeyword()));
        wrapper.eq(queryDTO.getCategoryId() != null, Product::getCategoryId, queryDTO.getCategoryId());
        wrapper.eq(queryDTO.getProductType() != null, Product::getProductType, queryDTO.getProductType());
        wrapper.eq(queryDTO.getStatus() != null, Product::getStatus, queryDTO.getStatus());
        wrapper.orderByDesc(Product::getCreatedAt);

        return page(page, wrapper);
    }

    @Override
    public ProductDetailVO getProductDetail(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "产品不存在");
        }

        ProductDetailVO vo = new ProductDetailVO();
        BeanUtils.copyProperties(product, vo);

        // 加载分类名称
        if (product.getCategoryId() != null) {
            ProductCategory category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        // 加载图片
        List<ProductImage> images = productImageService.getImagesByProductId(id);
        vo.setImages(images);

        // 加载BOM子项
        List<BomItem> bomItems = bomItemMapper.selectList(
                new LambdaQueryWrapper<BomItem>()
                        .eq(BomItem::getProductId, id)
                        .orderByAsc(BomItem::getSortOrder));
        // 填充物料信息
        for (BomItem item : bomItems) {
            Product material = getById(item.getMaterialId());
            if (material != null) {
                item.setMaterialName(material.getProductName());
                item.setMaterialCode(material.getProductCode());
                item.setMaterialUnit(material.getUnit());
                item.setMaterialSpec(material.getSpec());
                item.setMaterialType(material.getProductType());
            }
        }
        vo.setBomItems(bomItems);

        // 加载默认工艺路线（通过接口跨模块调用）
        ProcessRouteLookup.RouteSummary route = processRouteLookup.getDefaultRouteByProductId(id);
        if (route != null) {
            ProductDetailVO.ProcessRouteSummary summary = new ProductDetailVO.ProcessRouteSummary();
            summary.setId(route.getId());
            summary.setRouteCode(route.getRouteCode());
            summary.setRouteName(route.getRouteName());
            summary.setVersion(route.getVersion());
            if (route.getSteps() != null) {
                List<ProductDetailVO.StepSummary> stepSummaries = new ArrayList<>();
                for (ProcessRouteLookup.StepSummary step : route.getSteps()) {
                    ProductDetailVO.StepSummary ss = new ProductDetailVO.StepSummary();
                    ss.setStepNo(step.getStepNo());
                    ss.setStepName(step.getStepName());
                    ss.setStandardTime(step.getStandardTime());
                    ss.setEquipmentType(step.getEquipmentType());
                    ss.setDescription(step.getDescription());
                    stepSummaries.add(ss);
                }
                summary.setSteps(stepSummaries);
            }
            vo.setDefaultProcessRoute(summary);
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(ProductDTO dto) {
        // 检查产品编码唯一性
        long count = count(new LambdaQueryWrapper<Product>()
                .eq(Product::getProductCode, dto.getProductCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PRODUCT_CODE_EXISTS, "产品编码已存在");
        }

        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setStatus(1);
        save(product);
        return product.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long id, ProductDTO dto) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "产品不存在");
        }

        // 检查产品编码唯一性（排除自身）
        long count = count(new LambdaQueryWrapper<Product>()
                .eq(Product::getProductCode, dto.getProductCode())
                .ne(Product::getId, id));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PRODUCT_CODE_EXISTS, "产品编码已存在");
        }

        BeanUtils.copyProperties(dto, product);
        product.setId(id);
        updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "产品不存在");
        }

        // 检查是否被BOM引用
        long bomRefCount = bomItemMapper.selectCount(new LambdaQueryWrapper<BomItem>()
                .eq(BomItem::getMaterialId, id));
        if (bomRefCount > 0) {
            throw new BusinessException(ErrorCode.PRODUCT_REFERENCED_BY_BOM, "该产品被其他产品BOM引用，不能删除");
        }

        // 级联删除产品图片记录
        productImageService.deleteByProductId(id);

        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, Integer status) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "产品不存在");
        }
        product.setStatus(status);
        updateById(product);
    }
}
