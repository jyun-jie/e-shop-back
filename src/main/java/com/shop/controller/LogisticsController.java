package com.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.dto.*;
import com.shop.entity.Result;
import com.shop.service.LogisticsService;
import com.shop.service.UserService;
import com.shop.util.AesUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 物流控制器
 * 提供物流相关的API端点
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/Logistics")
public class LogisticsController {
    
    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private RedisTemplate<String , String> redisTemplate;

    @PreAuthorize("hasRole('User')")
    @PostMapping("/queryStoreMap")
    public Result queryStoreMap(@RequestBody StoreMapRequestDto request) {
        try {
            StoreMapResponseDto storeList = logisticsService.queryStoreMap(request);
            return Result.success(storeList);
        } catch (Exception e) {
            log.error("查詢地圖失敗", e);
            return Result.error("查詢失敗: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('User')")
    @PostMapping("/print")
    public Result printShippingLabel(@RequestBody PrintShippingLabelDto request) {
        try {
            StoreMapResponseDto printContent = logisticsService.printShippingLabel(request);
            return Result.success(printContent);
        } catch (Exception e) {
            log.error("列印寄貨單失敗", e);
            return Result.error("列印失敗: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('User')")
    @PostMapping("/query")
    public Result queryShipping(@RequestBody String request) {
        try {
            LogisticsStatusQueryDto printContent = logisticsService.queryShipping(request);
            return Result.success(printContent);
        } catch (Exception e) {
            log.error("搜尋寄貨單失敗", e);
            return Result.error("搜尋失败: " + e.getMessage());
        }
    }

    @PostMapping("/callback")
    public String handleLogisticsCallback(@RequestParam Map<String, String> params ) {
        try {
            String jsonDataString = params.get("JSONData") ; // Status , ,Message , Result
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            Map<String, Object> dataMap = objectMapper.readValue(jsonDataString, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});

            if ("SUCCESS".equals(dataMap.get("Status"))) {
                Object resultData = dataMap.get("Result");
                if (resultData == null) {
                    log.error("SUCCESS 狀態下，Result 欄位為 null");
                    return "0|Result is null";
                }
                log.info("收到物流狀態通知，Message 資料: {}", dataMap.get("Message"));
                LogisticsStatusCallbackDto callbackDto;

                if (resultData instanceof String) {
                    callbackDto = objectMapper.readValue((String) resultData, LogisticsStatusCallbackDto.class);
                } else {
                    callbackDto = objectMapper.convertValue(resultData, LogisticsStatusCallbackDto.class);
                }
                log.info("成功解析物流狀態通知: {}", callbackDto);

                logisticsService.handleLogisticsStatusCallback(callbackDto);

                return "1|OK";
            } else {
                String message = (String) dataMap.get("Message");
                log.warn("物流狀態通知非 SUCCESS，狀態: {}, 訊息: {}", dataMap.get("Status"), message);
                return "1|OK";
            }
        } catch (Exception e) {
            log.error("處理物流狀態通知失敗", e);
            return "0|" + e.getMessage();
        }
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping("/order/{orderId}")
    public Result getLogisticsOrderByBuyer(@PathVariable Integer orderId) {
        try {
            LogisticsOrderDto logisticsOrder = logisticsService.getLogisticsOrderByOrderId(orderId);
            if (logisticsOrder == null) {
                return Result.error("找不到物流信息");
            }
            return Result.success(logisticsOrder);
        } catch (Exception e) {
            log.error("查询物流信息失败", e);
            return Result.error("查詢失敗: " + e.getMessage());
        }
    }

    @PostMapping("/store/return")
    public void storeReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String orderNo = logisticsService.getStoreReturn(request) ;
        //更改
        response.sendRedirect("http://localhost:5173/checkOrder?orderNo=" + orderNo);
    }

    @GetMapping("/store/result")
    public Result getStoreResult(@RequestParam String orderNo) throws JsonProcessingException {
        StoreInfoDto store = logisticsService.getStoreResult(orderNo ) ;
        if(store == null){
            return Result.error("空值") ;
        }
        return  Result.success(store);
    }

    @GetMapping("/order")
    public Result getLogisticsOrder(@RequestParam String storeType ){
        List<LogisticsOrderDto> result = logisticsService.getLogisticsOrder(storeType) ;
        return Result.success(result);
    }
}
