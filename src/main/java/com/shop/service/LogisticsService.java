package com.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shop.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

/**
 * 物流服务接口
 */
public interface LogisticsService {
    
    /**
     * 查询门市地图（NPA-B51）
     * @param request 查询请求
     * @return 门市信息列表
     */
    StoreMapResponseDto queryStoreMap(StoreMapRequestDto request);
    
    /**
     * 建立物流寄货单（NPA-B52）
     * @param request 建立物流单请求
     * @return 物流单号（AllPayLogisticsID）
     */
    int createLogisticsOrder(CreateLogisticsOrderDto request) throws JsonProcessingException;
    
    /**
     * 列印寄货单（NPA-B54）
     * @param request 列印请求
     * @return 列印内容（HTML或PDF）
     */
    StoreMapResponseDto printShippingLabel(PrintShippingLabelDto request) throws IOException;
    
    /**
     * 处理物流状态通知（NPA-B58）
     * 当物流状态更新时，蓝新会调用此接口
     * @param callback 回调数据
     */
    void handleLogisticsStatusCallback(LogisticsStatusCallbackDto callback);
    
    /**
     * 根据订单ID查询物流单信息
     */
    LogisticsOrderDto getLogisticsOrderByOrderId(Integer orderId);

    Map<String, String> getMerchantOrderNo(String s) throws JsonProcessingException;

    String getStoreReturn(HttpServletRequest request) throws JsonProcessingException;
    StoreInfoDto getStoreResult(String orderNo ) throws JsonProcessingException;
    String getLogisticsOrderInfo(HttpServletRequest request) throws JsonProcessingException;

    LogisticsStatusQueryDto queryShipping (String queryNo) throws JsonProcessingException;

    List<LogisticsOrderDto> getLogisticsOrder(String storeType);
}
