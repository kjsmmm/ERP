package com.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.purchase.dto.SupplierDTO;
import com.erp.purchase.entity.PurchaseOrder;
import com.erp.purchase.entity.Supplier;
import com.erp.purchase.mapper.PurchaseOrderMapper;
import com.erp.purchase.mapper.SupplierMapper;
import com.erp.purchase.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    private final PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public IPage<Supplier> getSupplierPage(String keyword, Integer pageNum, Integer pageSize) {
        Page<Supplier> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Supplier::getSupplierCode, keyword)
                    .or()
                    .like(Supplier::getSupplierName, keyword)
                    .or()
                    .like(Supplier::getContactName, keyword);
        }
        wrapper.orderByDesc(Supplier::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    public List<Supplier> listAll() {
        return list(new LambdaQueryWrapper<Supplier>()
                .eq(Supplier::getStatus, 1)
                .orderByAsc(Supplier::getSupplierCode));
    }

    @Override
    public void createSupplier(SupplierDTO dto) {
        long count = count(new LambdaQueryWrapper<Supplier>()
                .eq(Supplier::getSupplierCode, dto.getSupplierCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.SUPPLIER_CODE_EXISTS, "供应商编码已存在");
        }
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(dto, supplier);
        supplier.setStatus(1);
        save(supplier);
    }

    @Override
    public void updateSupplier(Long id, SupplierDTO dto) {
        Supplier supplier = getById(id);
        if (supplier == null) {
            throw new BusinessException(ErrorCode.SUPPLIER_NOT_FOUND, "供应商不存在");
        }
        BeanUtils.copyProperties(dto, supplier);
        updateById(supplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        long count = purchaseOrderMapper.selectCount(
                new LambdaQueryWrapper<PurchaseOrder>().eq(PurchaseOrder::getSupplierId, id));
        if (count > 0) {
            throw new BusinessException(ErrorCode.SUPPLIER_NOT_FOUND, "供应商已被采购单引用，不能删除");
        }
        removeById(id);
    }
}
