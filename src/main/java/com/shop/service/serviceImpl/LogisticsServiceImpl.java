package com.shop.service.serviceImpl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.component.LogisticsClient;
import com.shop.dto.*;
import com.shop.entity.*;
import com.shop.mapper.*;
import com.shop.service.LogisticsService;
import com.shop.service.UserService;
import com.shop.util.AesUtil;
import io.lettuce.core.ScriptOutputType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LogisticsServiceImpl implements LogisticsService {
    
    @Autowired
    private LogisticsClient logisticsClient;
    
    @Autowired
    private LogisticsMapper logisticsMapper;
    
    @Autowired
    private BuyerOrderMapper buyerOrderMapper;
    
    @Autowired
    private MasterOrderMapper masterOrderMapper;
    
    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private UserService userService ;

    @Autowired
    private RedisTemplate<String , String > redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SellerServiceImpl sellerService;

    @Override
    public StoreMapResponseDto queryStoreMap(StoreMapRequestDto request) {

        StoreMapResponseDto response = logisticsClient.queryStoreMap(request);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createLogisticsOrder(CreateLogisticsOrderDto request) throws JsonProcessingException {
        log.info("建立物流單: orderId={}, storeType={}, isCod={}",
                request.getOrderId(), request.getStoreType(), request.getIsCod());

        Map<String, String> params = new HashMap<>();
        params.put("MerchantOrderNo" , request.getMerchantOrderNo());
        params.put("DeliveryType", request.getDeliveryType());  // C2C
        params.put("StoreType", request.getStoreType()) ;
        params.put("ReceiverStoreID", request.getStoreId());
        params.put("ReceiverStoreName", request.getStoreName());

        params.put("SenderName", request.getSenderName());
        
        params.put("ReceiverName", request.getReceiverName());
        params.put("ReceiverPhone", request.getReceiverPhone());
        params.put("ReceiverEmail", request.getReceiverEmail());

        params.put("GoodsAmount", request.getGoodsAmount().toString());

        params.put("CollectionAmount", String.valueOf((int) request.getAmount()));
        if (request.getIsCod() != null && request.getIsCod()) {
            params.put("IsCollection", "Y");
        } else {
            params.put("IsCollection", "N");
        }

        LogisticsCreateResponseDto response = logisticsClient.createLogisticsOrder(params);

        String allPayLogisticsId = null;
        try {

            JSONObject jsonResponse = new JSONObject(response.getDecryptData());
            if ("SUCCESS".equals(response.getStatus())) {
                allPayLogisticsId = jsonResponse.optString("TradeNo");

                LogisticsOrder logisticsOrder = new LogisticsOrder();

                logisticsOrder.setOrderId(request.getOrderId());
                logisticsOrder.setMasterOrderId(request.getMasterOrderId());
                logisticsOrder.setLogisticsType(request.getDeliveryType());

                logisticsOrder.setAllPayLogisticsId(allPayLogisticsId);
                logisticsOrder.setMerchantOrderNo(request.getMerchantOrderNo());

                logisticsOrder.setSellerId(request.getSellerId());
                logisticsOrder.setSenderName(request.getSenderName());

                logisticsOrder.setBuyerId(request.getBuyerId());
                logisticsOrder.setReceiverName(request.getReceiverName());
                logisticsOrder.setReceiverPhone(request.getReceiverPhone());
                logisticsOrder.setReceiverEmail(request.getReceiverEmail());

                logisticsOrder.setStoreType(request.getStoreType());
                logisticsOrder.setStoreId(request.getStoreId());
                logisticsOrder.setStoreName(request.getStoreName());

                logisticsOrder.setIsCod(request.getIsCod());
                logisticsOrder.setAmount(request.getAmount());
                logisticsOrder.setLogisticsStatus("0_1");  // 初始状态：订单未处理
                logisticsOrder.setLogisticsStatusDesc("訂單未處理");

                logisticsMapper.insert(logisticsOrder);

                buyerOrderMapper.updateLogisticsOrderId(request.getOrderId(), logisticsOrder.getId());

                log.info("物流單創建成功: orderId={}, logisticsId={}",
                        request.getOrderId(), allPayLogisticsId);
            } else {
                String errorMsg = jsonResponse.optString("Message");
                log.error("物流單創建失敗: {}", errorMsg);
                throw new RuntimeException("建立物流單失敗: " + errorMsg);
            }
        } catch (Exception e) {
            log.error("解析物流單創建失敗", e);
            throw new RuntimeException("建立物流單失敗: " + e.getMessage());
        }

        return request.getOrderId();
    }

    @Override
    public StoreMapResponseDto printShippingLabel(PrintShippingLabelDto request) throws IOException {
        log.info("列印寄貨單: logisticsOrderIds={}", request.getLogisticsOrderIds());
        
        // 查询物流单信息
        List<String> allPayLogisticsMerchantOrderNo = new ArrayList<>();
        for (Integer logisticsOrderId : request.getLogisticsOrderIds()) {
            LogisticsOrder logisticsOrder = logisticsMapper.findById(logisticsOrderId);
            LogisticsStatusQueryDto query = new LogisticsStatusQueryDto() ;

            if (logisticsOrder != null && logisticsOrder.getAllPayLogisticsId() != null) {
                allPayLogisticsMerchantOrderNo.add(logisticsOrder.getMerchantOrderNo());

                query.setMerchantOrderNo(logisticsOrder.getMerchantOrderNo());
                query.setRetId(logisticsOrder.getLogisticsStatus());
                query.setRetString(logisticsOrder.getLogisticsStatusDesc());

                logisticsMapper.updateStatus(query , true);
            }
        }
        
        if (allPayLogisticsMerchantOrderNo.isEmpty()) {
            throw new RuntimeException("沒有可列印的物流單");
        }

        StoreMapResponseDto printContent = logisticsClient.printShippingLabel(allPayLogisticsMerchantOrderNo, request.getStoreType());

        return printContent;
    }


    @Override
    public LogisticsStatusQueryDto queryShipping(String queryNo) throws JsonProcessingException {

        LogisticsStatusQueryDto  result = logisticsClient.queryShipping(queryNo);
        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleLogisticsStatusCallback(LogisticsStatusCallbackDto callback) {
        log.info("收到物流狀態通知: MerchantOrderNo={} 取貨完成",
                callback.getMerchantOrderNo());

        LogisticsOrder logisticsOrder = logisticsMapper.findByMerchantOrderNo(callback.getMerchantOrderNo());
        if (logisticsOrder == null) {
            log.error("找不到物流單: {}", callback.getMerchantOrderNo());
            throw new RuntimeException("物流單不存在");
        }

        LogisticsStatusQueryDto logisticsStatusQueryDto = new LogisticsStatusQueryDto() ;
        logisticsStatusQueryDto.setMerchantOrderNo(callback.getMerchantOrderNo());
        logisticsStatusQueryDto.setRetId("6");
        logisticsStatusQueryDto.setRetString("買家取貨完成");

        logisticsMapper.updateStatus(
                logisticsStatusQueryDto , false
        );

        updateOrderStatusByLogisticsStatus(logisticsOrder, "6");
        
        log.info("物流狀態更新完成: orderId={}, status={}",
                logisticsOrder.getOrderId(), "6");
    }

    private void updateOrderStatusByLogisticsStatus(LogisticsOrder logisticsOrder, String retId) {
        Order order = buyerOrderMapper.selectOrderById(logisticsOrder.getOrderId());
        if (order == null) {
            return;
        }
        
        OrderState newState = null;

        if ("0_1".equals(retId) || "0_2".equals(retId) || "0_3".equals(retId)) {
            newState = OrderState.Not_Ship;
        } else if ("1".equals(retId) || "2".equals(retId) || "3".equals(retId) || "4".equals(retId)) {
            newState = OrderState.Shipping;
        } else if ("5".equals(retId)) {
            newState = OrderState.ReadyForPickup;
        } else if ("6".equals(retId)) {
            newState = OrderState.Complete;

            if (logisticsOrder.getIsCod() != null && logisticsOrder.getIsCod()) {
                handleCodPayment(logisticsOrder, order);
            }
        } else if (retId.startsWith("-")) {
            log.warn("物流異常狀態: orderId={}, retId={}", order.getId(), retId);
        }

        if (newState != null && order.getState() != newState) {
            buyerOrderMapper.updateOrderState(order.getId(), newState);
            log.info("訂單狀態更新: orderId={}, oldState={}, newState={}",
                    order.getId(), order.getState(), newState);
        }
    }

    private void handleCodPayment(LogisticsOrder logisticsOrder, Order order) {
        log.info("處理取貨付款: orderId={}, codAmount={}",
                order.getId(), logisticsOrder.getAmount());

        String tradeNo = logisticsOrder.getMerchantOrderNo();

        Payment payment = paymentMapper.findByTradeNoForUpdate(tradeNo);
        paymentMapper.updateStatus(payment.getId() , paymentStatus.PAID.toString()); ;
        masterOrderMapper.updateStatus(payment.getMaster_order_id() , paymentStatus.PAID.toString());
        
        log.info("取貨付款處理完成: orderId={}, paymentId={}", order.getId(), payment.getId());
    }
    
    @Override
    public LogisticsOrderDto getLogisticsOrderByOrderId(Integer orderId) {
        int userId = userService.findIdbyName();
        LogisticsOrder logisticsOrder = logisticsMapper.findByOrderId(orderId , userId);
        if (logisticsOrder == null) {
            return null;
        }

        LogisticsOrderDto dto = new LogisticsOrderDto();
        BeanUtils.copyProperties(logisticsOrder, dto);
        return dto;
    }

    @Override
    public List<LogisticsOrderDto> getLogisticsOrder(String storeType) {
        Seller seller = sellerService.getActiveSellerOrThrow();

        List<LogisticsOrderDto> logisticsOrderList = logisticsMapper.getLogisticsOrderByStoreTypeAndSellerId(storeType , seller.getId());

        List<LogisticsOrderDto> logisticsOrderDtoList = new ArrayList<>() ;
        if(logisticsOrderList  != null ){
            for(LogisticsOrderDto logisticsOrder : logisticsOrderList ){
                LogisticsOrderDto logisticsOrderDto = new LogisticsOrderDto();
                logisticsOrderDto.setOrderId(logisticsOrder.getOrderId());
                logisticsOrderDto.setLogisticsStatus(logisticsOrder.getLogisticsStatusDesc());

                logisticsOrderDtoList.add(logisticsOrderDto);
            }
        }

        return logisticsOrderDtoList;
    }

    @Override
    public String getStoreReturn(HttpServletRequest request) throws JsonProcessingException {
        Map<String, String> storeData = new HashMap<>();
        Map<String, String> orderInfo ;
        String orderNo = "";


        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue()[0];
            if ("EncryptData".equals(k)) {
                try {
                    orderInfo = getMerchantOrderNo(v);
                    orderNo = orderInfo.get("MerchantOrderNo");

                    for (Map.Entry<String, String> orderInfoItem : orderInfo.entrySet()) {
                        storeData.put(orderInfoItem.getKey(), orderInfoItem.getValue());
                    }

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(storeData);

        redisTemplate.opsForValue()
                .set("STORE:" + orderNo, json, 10, TimeUnit.MINUTES);

        return orderNo ;
    }

    // 需改名
    public Map<String, String> getMerchantOrderNo(String encodeOnject) throws JsonProcessingException {

        String UTF8EncodeObject = URLDecoder.decode(encodeOnject, StandardCharsets.UTF_8);
        String decrypted = logisticsClient.decryptAES(UTF8EncodeObject);


        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> dataMap = mapper.readValue(decrypted, new TypeReference<Map<String, String>>() {});
        return dataMap;
    }

    @Override
    public StoreInfoDto getStoreResult(String orderNo ) throws JsonProcessingException {
        String store = redisTemplate.opsForValue().get("STORE:" + orderNo);

        if(store == null){
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String , String> json = objectMapper.readValue(store , Map.class);

        StoreInfoDto storeInfoDto = new StoreInfoDto();
        storeInfoDto.setStoreId(json.get("StoreID"));
        storeInfoDto.setStoreName(json.get("StoreName"));
        storeInfoDto.setStoreAddress(json.get("StoreAddr"));
        storeInfoDto.setLgsType(json.get("LgsType"));
        storeInfoDto.setStoreType(json.get("ShipType"));

        System.out.println(storeInfoDto);
        return storeInfoDto ;
    }

}
