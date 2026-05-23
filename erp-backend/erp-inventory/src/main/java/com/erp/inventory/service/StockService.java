package com.erp.inventory.service;

import com.erp.inventory.dto.StockInDTO;
import com.erp.inventory.dto.StockOutDTO;

public interface StockService {

    void stockIn(StockInDTO dto);

    void stockOut(StockOutDTO dto);
}
