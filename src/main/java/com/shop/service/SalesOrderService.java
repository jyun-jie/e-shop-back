package com.shop.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.shop.dto.SalesOrderDto;
import com.shop.dto.SentShipOrderDto;
import com.shop.entity.Order;

import java.util.List;

public interface SalesOrderService {
    public List<SalesOrderDto> getSalesOrderByState(String mode , String orderState ,String storeType);

    public boolean sentShippedOrders(SentShipOrderDto shippedOrderId);

    public List<SalesOrderDto> getNonCreatedLogisticsOrders(String storeType) ;
    /**
     * 为订单创建物流单（C2C店到店取货付款）
     * 卖家准备出货时调用
     */
    public String createLogisticsForOrder(com.shop.dto.CreateLogisticsForOrderDto request) throws JsonProcessingException;
}
