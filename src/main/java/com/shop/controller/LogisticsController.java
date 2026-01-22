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

    /**
     * 查詢門市地圖（NPA-B51）
     */
    @PreAuthorize("hasRole('User')")
    @PostMapping("/queryStoreMap")
    public Result queryStoreMap(@RequestBody StoreMapRequestDto request) {
        try {
            StoreMapResponseDto storeList = logisticsService.queryStoreMap(request);
            return Result.success(storeList);
        } catch (Exception e) {
            log.error("查询门市地图失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 建立物流寄貨單（NPA-B52）
     */
//    @PreAuthorize("hasRole('User')")
//    @PostMapping("/create")
//    public Result createLogisticsOrder(@RequestBody CreateLogisticsOrderDto request) {
//        try {
//            int orderId = logisticsService.createLogisticsOrder(request);
//            if(orderId >= 1 ){
//                return Result.success("訂單編號" + orderId);
//            }
//        } catch (Exception e) {
//            log.error("建立物流单失败", e);
//            return Result.error("建立失败: " + e.getMessage());
//        }
//        return Result.error("no info");
//    }


    
    /**
     * 列印寄貨單（NPA-B54）
     */
    @PreAuthorize("hasRole('User')")
    @PostMapping("/print")
    public Result printShippingLabel(@RequestBody PrintShippingLabelDto request) {
        try {
            StoreMapResponseDto printContent = logisticsService.printShippingLabel(request);
            return Result.success(printContent);
        } catch (Exception e) {
            log.error("列印寄货单失败", e);
            return Result.error("列印失败: " + e.getMessage());
        }
    }

    //有
    @PreAuthorize("hasRole('User')")
    @PostMapping("/query")
    public Result queryShipping(@RequestBody String request) {
        try {
            LogisticsStatusQueryDto printContent = logisticsService.queryShipping(request);
            return Result.success(printContent);
        } catch (Exception e) {
            log.error("列印寄货单失败", e);
            return Result.error("列印失败: " + e.getMessage());
        }
    }



    
    /**
     * 物流狀態通知接收（NPA-B58）

     */
    @PostMapping("/callback")
    public String handleLogisticsCallback(@RequestParam Map<String, String> params ) {
        try {

            System.out.println(params);
            // 将Map转换为DTO

            String jsonDataString = params.get("JSONData") ; // Status , ,Message , Result
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            Map<String, Object> dataMap = objectMapper.readValue(jsonDataString, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});


            if ("SUCCESS".equals(dataMap.get("Status"))) {
                // 3. 取得 Result 欄位，它本身是一個 Map
                Object resultData = dataMap.get("Result");
                if (resultData == null) {
                    log.error("SUCCESS 狀態下，Result 欄位為 null");
                    return "0|Result is null";
                }

                log.info("收到物流狀態通知，Message 資料: {}", dataMap.get("Message"));
                // 4. 【核心修正】判斷 Result 是 JSON 字串還是 Map 物件
                LogisticsStatusCallbackDto callbackDto;

                if (resultData instanceof String) {
                    // 如果是字串（例如 "{\"MerchantID\":...}"），使用 readValue 解析 JSON 字串
                    callbackDto = objectMapper.readValue((String) resultData, LogisticsStatusCallbackDto.class);
                } else {
                    // 如果已經是 Map/Object，使用 convertValue 轉換
                    callbackDto = objectMapper.convertValue(resultData, LogisticsStatusCallbackDto.class);
                }

                log.info("成功解析物流狀態通知: {}", callbackDto);

                // 5. 呼叫 service 處理後續邏輯，傳入的是包含完整資料的 DTO
                logisticsService.handleLogisticsStatusCallback(callbackDto);

                // 返回成功響應
                return "1|OK";
            } else {
                // 處理狀態不是 SUCCESS 的情況
                String message = (String) dataMap.get("Message");
                log.warn("物流狀態通知非 SUCCESS，狀態: {}, 訊息: {}", dataMap.get("Status"), message);
                // 即使業務失敗，通常也需回覆藍新成功接收通知，所以仍回傳 "1|OK"
                return "1|OK";
            }
        } catch (Exception e) {
            log.error("处理物流状态通知失败", e);
            // 返回失败响应
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
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    //有
    @PostMapping("/store/return")
    public void storeReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String orderNo = logisticsService.getStoreReturn(request) ;

        response.sendRedirect("http://localhost:5173/checkOrder?orderNo=" + orderNo);
    }

    //有
    @GetMapping("/store/result")
    public Result getStoreResult(@RequestParam String orderNo) throws JsonProcessingException {
        StoreInfoDto store = logisticsService.getStoreResult(orderNo ) ;
        if(store == null){
            return Result.error("空值") ;
        }
        return  Result.success(store);
    }

    //有
    @GetMapping("/order")
    public Result getLogisticsOrder(@RequestParam String storeType ){
        System.out.println(storeType);
        List<LogisticsOrderDto> result = logisticsService.getLogisticsOrder(storeType) ;
        return Result.success(result);
    }
}
