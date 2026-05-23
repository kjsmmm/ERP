package com.erp.order.controller;

import com.erp.common.result.Result;
import com.erp.order.entity.CustomerProductPrice;
import com.erp.order.service.CustomerPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customer-prices")
@RequiredArgsConstructor
public class CustomerPriceController {

    private final CustomerPriceService customerPriceService;

    @GetMapping("/customer/{customerId}")
    public Result<List<CustomerProductPrice>> getByCustomerId(@PathVariable Long customerId) {
        return Result.success(customerPriceService.getByCustomerId(customerId));
    }

    @GetMapping("/customer/{customerId}/product/{productId}")
    public Result<BigDecimal> getPrice(@PathVariable Long customerId, @PathVariable Long productId) {
        BigDecimal price = customerPriceService.getPrice(customerId, productId);
        return Result.success(price);
    }

    @PostMapping
    public Result<Void> savePrice(@RequestParam Long customerId,
                                  @RequestParam Long productId,
                                  @RequestParam BigDecimal price,
                                  @RequestParam(required = false) String remark) {
        customerPriceService.saveOrUpdatePrice(customerId, productId, price, remark);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePrice(@PathVariable Long id) {
        customerPriceService.deletePrice(id);
        return Result.success();
    }
}
