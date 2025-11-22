package com.shop.service;


import com.shop.dto.SalesOrderDto;
import com.shop.entity.OrderState;


import java.util.List;

public interface SalesOrderService {
    public List<SalesOrderDto> getSalesOrders(String orderState);

    public boolean sentShippedOrders(List<Integer> shippedOrderId);
}
