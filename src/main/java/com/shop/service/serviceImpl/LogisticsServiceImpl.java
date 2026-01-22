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

/**
 * 物流服务实现类
 * 负责处理所有物流相关的业务逻辑
 */
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

    /**
     * 查询门市地图（NPA-B51）
     * 
     * 概念说明：
     * - 买家在下单时，需要选择取货门市
     * - 此API根据超商类型、城市或地址查询可用的门市列表
     * - 返回的门市信息包括门市代码、名称、地址等
     */
    @Override
    public StoreMapResponseDto queryStoreMap(StoreMapRequestDto request) {

        StoreMapResponseDto response = logisticsClient.queryStoreMap(request);
        return response;
    }
    
    /**
     * 建立物流寄货单（NPA-B52）
     * 
     * 概念说明：
     * - 卖家准备出货时，调用此API创建物流单
     * - 系统会将订单信息、收寄件人信息、门市信息等传给蓝新
     * - 蓝新返回物流单号（AllPayLogisticsID），用于后续追踪
     * - 如果是取货付款（COD），需要传入订单金额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createLogisticsOrder(CreateLogisticsOrderDto request) throws JsonProcessingException {
        log.info("建立物流单: orderId={}, storeType={}, isCod={}", 
                request.getOrderId(), request.getStoreType(), request.getIsCod());
        
        // 查询订单信息
        //OrderPaymentJoinDto order = buyerOrderMapper.selectOrderByMasterOrderId(request.getOrderId());
//        if (order == null) {
//            throw new RuntimeException("订单不存在: " + request.getOrderId());
//        }
//
//        // 检查订单状态
//        if (order.getState() != OrderState.Not_Ship) {
//            throw new RuntimeException("订单状态不正确，无法建立物流单: " + order.getState());
//        }
//
//        System.out.println(order);

        // 构建API请求参数
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
        
        // 取货付款设置
        if (request.getIsCod() != null && request.getIsCod()) {
            params.put("IsCollection", "Y");
            params.put("CollectionAmount", String.valueOf((int) request.getCodAmount()));
        } else {
            params.put("IsCollection", "N");
            params.put("CollectionAmount", "0");
        }

        // 调用蓝新API
        LogisticsCreateResponseDto response = logisticsClient.createLogisticsOrder(params);

        // 解析响应
        String allPayLogisticsId = null;
        try {

            JSONObject jsonResponse = new JSONObject(response.getDecryptData());
            if ("SUCCESS".equals(response.getStatus())) {
                allPayLogisticsId = jsonResponse.optString("TradeNo");

                System.out.println("all:"+jsonResponse);

                // 保存物流单到数据库
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
                logisticsOrder.setCodAmount(request.getCodAmount());
                logisticsOrder.setLogisticsStatus("0_1");  // 初始状态：订单未处理
                logisticsOrder.setLogisticsStatusDesc("訂單未處理");

                logisticsMapper.insert(logisticsOrder);

                // 更新订单的物流单ID
                buyerOrderMapper.updateLogisticsOrderId(request.getOrderId(), logisticsOrder.getId());

                log.info("物流单创建成功: orderId={}, logisticsId={}",
                        request.getOrderId(), allPayLogisticsId);
            } else {
                String errorMsg = jsonResponse.optString("Message");
                log.error("建立物流单失败: {}", errorMsg);
                throw new RuntimeException("建立物流单失败: " + errorMsg);
            }
        } catch (Exception e) {
            log.error("解析物流单创建响应失败", e);
            throw new RuntimeException("建立物流单失败: " + e.getMessage());
        }

        return request.getOrderId();
    }
    
    /**
     * 列印寄货单（NPA-B54）
     * 
     * 概念说明：
     * - 卖家建立物流单后，需要列印寄货单标签
     * - 此API返回HTML或PDF格式的列印内容
     * - 卖家可以列印后贴在包裹上，送到超商门市寄件
     */
    @Override
    public StoreMapResponseDto printShippingLabel(PrintShippingLabelDto request) throws IOException {
        log.info("列印寄货单: logisticsOrderIds={}", request.getLogisticsOrderIds());
        
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
            throw new RuntimeException("没有可列印的物流单");
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
        log.info("收到物流状态通知: MerchantOrderNo={} 取貨完成",
                callback.getMerchantOrderNo());

        // 查询物流单
        LogisticsOrder logisticsOrder = logisticsMapper.findByMerchantOrderNo(callback.getMerchantOrderNo());
        if (logisticsOrder == null) {
            log.error("找不到物流单: {}", callback.getMerchantOrderNo());
            throw new RuntimeException("物流单不存在");
        }


        LogisticsStatusQueryDto logisticsStatusQueryDto = new LogisticsStatusQueryDto() ;
        logisticsStatusQueryDto.setMerchantOrderNo(callback.getMerchantOrderNo());
        logisticsStatusQueryDto.setRetId("6");
        logisticsStatusQueryDto.setRetString("買家取貨完成");

        // 更新物流状态 修改
        logisticsMapper.updateStatus(
                logisticsStatusQueryDto , false
        );
        
        // 根据状态码更新订单状态
        updateOrderStatusByLogisticsStatus(logisticsOrder, "6");
        
        log.info("物流状态更新完成: orderId={}, status={}", 
                logisticsOrder.getOrderId(), "6");
    }

    private void updateOrderStatusByLogisticsStatus(LogisticsOrder logisticsOrder, String retId) {
        Order order = buyerOrderMapper.selectOrderById(logisticsOrder.getOrderId());
        if (order == null) {
            return;
        }
        
        OrderState newState = null;
        
        // 根据RetId判断订单状态
        if ("0_1".equals(retId) || "0_2".equals(retId) || "0_3".equals(retId)) {
            newState = OrderState.Not_Ship;
        } else if ("1".equals(retId) || "2".equals(retId) || "3".equals(retId) || "4".equals(retId)) {
            newState = OrderState.Shipping;
        } else if ("5".equals(retId)) {
            newState = OrderState.ReadyForPickup;
        } else if ("6".equals(retId)) {
            // 买家取货完成
            newState = OrderState.Complete;
            
            // 如果是取货付款（COD），需要创建Payment记录
            if (logisticsOrder.getIsCod() != null && logisticsOrder.getIsCod()) {
                handleCodPayment(logisticsOrder, order);
            }
        } else if (retId.startsWith("-")) {
            // 退货或异常状态，根据具体情况处理
            // 这里可以设置为CANCELLED或其他状态
            log.warn("物流异常状态: orderId={}, retId={}", order.getId(), retId);
        }
        
        // 更新订单状态
        if (newState != null && order.getState() != newState) {
            buyerOrderMapper.updateOrderState(order.getId(), newState);
            log.info("订单状态更新: orderId={}, oldState={}, newState={}", 
                    order.getId(), order.getState(), newState);
        }
    }

    private void handleCodPayment(LogisticsOrder logisticsOrder, Order order) {
        log.info("處理取貨付款: orderId={}, codAmount={}",
                order.getId(), logisticsOrder.getCodAmount());
        
        // 检查是否已经创建过Payment（幂等保护）
        // 这里简化处理，实际应该查询是否已存在
        String tradeNo = logisticsOrder.getMerchantOrderNo();

        Payment payment = paymentMapper.findByTradeNoForUpdate(tradeNo);
        paymentMapper.updateStatus(payment.getId() , paymentStatus.PAID.toString()); ;
        
        // 更新MasterOrder支付状态
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
        int userId  = userService.findIdbyName();
        List<LogisticsOrderDto> logisticsOrderList = logisticsMapper.getLogisticsOrderByStoreTypeAndUserId(storeType , userId);
        System.out.println(logisticsOrderList );

        List<LogisticsOrderDto> logisticsOrderDtoList = new ArrayList<>() ;
        if(logisticsOrderList  != null ){
            for(LogisticsOrderDto logisticsOrder : logisticsOrderList ){
                LogisticsOrderDto logisticsOrderDto = new LogisticsOrderDto();
                logisticsOrderDto.setOrderId(logisticsOrder.getId());
                logisticsOrderDto.setLogisticsStatus(logisticsOrder.getLogisticsStatus());

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
            //System.out.println("k:" + k + " v:" + v);
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
        System.out.println("orderNo" + orderNo);

        return orderNo ;
    }

    // 需改名
    public Map<String, String> getMerchantOrderNo(String encodeOnject) throws JsonProcessingException {

        String UTF8EncodeObject = URLDecoder.decode(encodeOnject, StandardCharsets.UTF_8);
        String decrypted = logisticsClient.decryptAES(UTF8EncodeObject);


        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> dataMap = mapper.readValue(decrypted, new TypeReference<Map<String, String>>() {});
        //System.out.println("DATA:" + dataMap );
        //System.out.println("DATAGET:" + dataMap.get("MerchantOrderNo") );
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

    @Override
    public String getLogisticsOrderInfo(HttpServletRequest request) throws JsonProcessingException {
        Map<String, String> storeData = new HashMap<>();
        Map<String, String> orderInfo ;
        String orderNo = "";


        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue()[0];
            System.out.println("k:" + k + " v:" + v);
            if("MerchantOrderNo".equals(k)){

                System.out.println(v);



            }
        }



        return orderNo ;
    }
}
