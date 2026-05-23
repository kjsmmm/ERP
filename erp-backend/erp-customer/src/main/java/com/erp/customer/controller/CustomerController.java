package com.erp.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.customer.dto.CustomerDTO;
import com.erp.customer.dto.CustomerQueryDTO;
import com.erp.customer.entity.Customer;
import com.erp.customer.service.CustomerService;
import com.erp.customer.vo.CustomerDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 客户管理控制器
 */
@Tag(name = "客户管理", description = "客户CRUD操作")
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "分页查询客户")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('customer:view')")
    public Result<IPage<Customer>> page(CustomerQueryDTO queryDTO) {
        IPage<Customer> page = customerService.getCustomerPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取客户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:view')")
    public Result<CustomerDetailVO> getById(@PathVariable Long id) {
        CustomerDetailVO detail = customerService.getCustomerDetail(id);
        return Result.success(detail);
    }

    @Operation(summary = "创建客户")
    @PostMapping
    @PreAuthorize("hasAuthority('customer:add')")
    @Log(module = "客户管理", operation = "创建客户")
    public Result<Long> create(@Valid @RequestBody CustomerDTO dto) {
        Long customerId = customerService.createCustomer(dto);
        return Result.success(customerId);
    }

    @Operation(summary = "更新客户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:edit')")
    @Log(module = "客户管理", operation = "更新客户")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CustomerDTO dto) {
        customerService.updateCustomer(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:delete')")
    @Log(module = "客户管理", operation = "删除客户")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Result.success();
    }

    @Operation(summary = "修改客户状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('customer:edit')")
    @Log(module = "客户管理", operation = "修改客户状态")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        customerService.changeStatus(id, status);
        return Result.success();
    }
}
