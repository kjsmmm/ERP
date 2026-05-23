package com.erp.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.order.dto.OrderDTO;
import com.erp.order.dto.OrderQueryDTO;
import com.erp.order.entity.SalesOrder;
import com.erp.order.vo.OrderDetailVO;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<SalesOrder> {

    /**
     * 创建订单
     */
    Long createOrder(OrderDTO dto);

    /**
     * 订单分页查询
     */
    IPage<SalesOrder> getOrderPage(OrderQueryDTO queryDTO);

    /**
     * 订单详情
     */
    OrderDetailVO getOrderDetail(Long id);

    /**
     * 更新订单
     */
    void updateOrder(Long id, OrderDTO dto);

    /**
     * 删除订单（仅草稿）
     */
    void deleteOrder(Long id);

    /**
     * 确认订单
     */
    void confirmOrder(Long id);

    /**
     * 取消订单
     */
    void cancelOrder(Long id);

    /**
     * 完成订单
     */
    void completeOrder(Long id);

    /**
     * 关闭订单
     */
    void closeOrder(Long id);

    /**
     * 审批通过回调（应用变更）
     */
    void applyOrderChange(String processInstanceId);

    /**
     * 审批驳回回调（丢弃变更）
     */
    void discardOrderChange(String processInstanceId);
}
