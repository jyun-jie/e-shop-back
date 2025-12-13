package com.shop.service;


import com.shop.dto.SalesOrderDto;
import com.shop.dto.SentShipOrderDto;

import java.util.List;

public interface SalesOrderService {
    public List<SalesOrderDto> getSalesOrderByState(String orderState);

    public boolean sentShippedOrders(SentShipOrderDto shippedOrderId);
}
