package com.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shop.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

public interface LogisticsService {

    StoreMapResponseDto queryStoreMap(StoreMapRequestDto request);

    int createLogisticsOrder(CreateLogisticsOrderDto request) throws JsonProcessingException;

    StoreMapResponseDto printShippingLabel(PrintShippingLabelDto request) throws IOException;

    void handleLogisticsStatusCallback(LogisticsStatusCallbackDto callback);

    LogisticsOrderDto getLogisticsOrderByOrderId(Integer orderId);

    Map<String, String> getMerchantOrderNo(String s) throws JsonProcessingException;

    String getStoreReturn(HttpServletRequest request) throws JsonProcessingException;
    StoreInfoDto getStoreResult(String orderNo ) throws JsonProcessingException;

    LogisticsStatusQueryDto queryShipping (String queryNo) throws JsonProcessingException;

    List<LogisticsOrderDto> getLogisticsOrder(String storeType);
}
