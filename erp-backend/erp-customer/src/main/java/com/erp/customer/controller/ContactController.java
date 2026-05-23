package com.erp.customer.controller;

import com.erp.common.annotation.Log;
import com.erp.common.result.Result;
import com.erp.customer.dto.ContactDTO;
import com.erp.customer.entity.CustomerContact;
import com.erp.customer.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户联系人控制器
 */
@Tag(name = "客户联系人", description = "联系人CRUD操作")
@RestController
@RequestMapping("/customer/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @Operation(summary = "获取客户联系人列表")
    @GetMapping("/list/{customerId}")
    @PreAuthorize("hasAuthority('customer:view')")
    public Result<List<CustomerContact>> list(@PathVariable Long customerId) {
        List<CustomerContact> contacts = contactService.getContactsByCustomerId(customerId);
        return Result.success(contacts);
    }

    @Operation(summary = "获取联系人详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:view')")
    public Result<CustomerContact> getById(@PathVariable Long id) {
        CustomerContact contact = contactService.getById(id);
        return Result.success(contact);
    }

    @Operation(summary = "创建联系人")
    @PostMapping
    @PreAuthorize("hasAuthority('customer:edit')")
    @Log(module = "客户联系人", operation = "创建联系人")
    public Result<Long> create(@Valid @RequestBody ContactDTO dto) {
        Long contactId = contactService.createContact(dto);
        return Result.success(contactId);
    }

    @Operation(summary = "更新联系人")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:edit')")
    @Log(module = "客户联系人", operation = "更新联系人")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ContactDTO dto) {
        contactService.updateContact(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除联系人")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:edit')")
    @Log(module = "客户联系人", operation = "删除联系人")
    public Result<Void> delete(@PathVariable Long id) {
        contactService.deleteContact(id);
        return Result.success();
    }
}
