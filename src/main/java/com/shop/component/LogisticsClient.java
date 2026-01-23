package com.shop.component;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.Exception.BusinessException;
import com.shop.dto.*;
import com.shop.mapper.LogisticsMapper;
import com.shop.util.AesUtil;
import io.lettuce.core.ScriptOutputType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 藍新物流API客戶端
 * 負責與藍新物流系統進行API交互
 */
@Slf4j
@Component
public class LogisticsClient {

    private final LogisticsMapper logisticsMapper;
    @Value("${newebpay.merchant-id}")
    private String merchantId;

    @Value("${newebpay.hash-key}")
    private String hashKey;

    @Value("${newebpay.hash-iv}")
    private String hashIv;

    @Value("${newebpay.logistics-completed-callback}")
    String logisticsCompletedNotify;

    @Value("${newebpay.logistics-store-return}")
    String logisticsStoreReturn;

    private final RestTemplate restTemplate;

    // 藍新物流API端點
    private static final String LOGISTICS_API_BASE = "https://ccore.newebpay.com";
    private static final String STORE_MAP_API = LOGISTICS_API_BASE + "/API/Logistic/storeMap";          // NPA-B51
    private static final String CREATE_LOGISTICS_API = LOGISTICS_API_BASE + "/API/Logistic/createShipment";          // NPA-B52
    private static final String PRINT_LABEL_API = LOGISTICS_API_BASE + "/API/Logistic/printLabel";  // NPA-B54
    private static final String QUERY_LOGISTICS_API = LOGISTICS_API_BASE + "/API/Logistic/queryShipment";     //NPA-B53


    public LogisticsClient(LogisticsMapper logisticsMapper) {
        this.restTemplate = new RestTemplate();
        this.logisticsMapper = logisticsMapper;
    }

    public StoreMapResponseDto queryStoreMap(StoreMapRequestDto request) {


        String MerchantOrderNo = "NP" + System.currentTimeMillis();
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);

        Map<String, Object> encryptMap = new HashMap<>();
        encryptMap.put("MerchantOrderNo", MerchantOrderNo);
        encryptMap.put("LgsType", request.getLgsType());
        encryptMap.put("ShipType", request.getShipType());
        encryptMap.put("ReturnURL", logisticsStoreReturn);
        encryptMap.put("TimeStamp", timeStamp);

        String encryptData = encryptAES(encryptMap);
        String hashData = compressureSHA(encryptData);

        StoreMapResponseDto response = new StoreMapResponseDto();
        response.setActionUrl(STORE_MAP_API);

        Map<String, String> formData = new HashMap<>();
        formData.put("UID_", merchantId);
        formData.put("EncryptData_", encryptData);
        formData.put("HashData_", hashData);
        formData.put("Version_", "1.0");
        formData.put("RespondType_", "JSON");

        response.setFormData(formData);
        return response;

    }

    public LogisticsCreateResponseDto createLogisticsOrder(Map<String , String> orderInfo) throws JsonProcessingException {
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);

        Map<String, Object> params = new HashMap<>();
        params.put("MerchantOrderNo", orderInfo.get("MerchantOrderNo"));
        params.put("TradeType", 1);
        params.put("UserName", orderInfo.get("ReceiverName"));
        params.put("UserTel", orderInfo.get("ReceiverPhone"));
        params.put("UserEmail", orderInfo.get("ReceiverEmail"));
        params.put("StoreID", orderInfo.get("ReceiverStoreID"));
        params.put("Amt", orderInfo.get("CollectionAmount"));
        params.put("NotifyURL" , logisticsCompletedNotify) ;
        params.put("LgsType", orderInfo.get("DeliveryType"));
        params.put("ShipType", orderInfo.get("StoreType"));
        params.put("TimeStamp", timestamp);

        String encryptData = encryptAES(params);
        String hashData = compressureSHA(encryptData);

        Map<String, String> formData = new HashMap<>();
        formData.put("UID_", merchantId);
        formData.put("EncryptData_", encryptData);
        formData.put("HashData_", hashData);
        formData.put("Version_", "1.0");
        formData.put("RespondType_", "JSON");

        String response = sendPostRequest(CREATE_LOGISTICS_API, formData);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.readValue(response, Map.class);

        String status = (String) result.get("Status");
        if (!"SUCCESS".equals(status)) {
            String message = (String) result.get("Message");
            throw new RuntimeException("查詢失敗: " + message);
        }

        // 解密回傳的資料
        String encryptedResult = (String) result.get("EncryptData");
        String decryptedData = decryptAES(encryptedResult);
        log.info("解密後資料: {}", decryptedData);

        LogisticsCreateResponseDto responseDto = new LogisticsCreateResponseDto() ;
        responseDto.setStatus((String) result.get("Status"));
        responseDto.setDecryptData(decryptedData);


        return  responseDto;
    }

    public StoreMapResponseDto printShippingLabel(List<String> allPayLogisticsMerchantOrderNo,
                                                  String storeType
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("LgsType", "C2C");
        params.put("ShipType", storeType);
        params.put("MerchantOrderNo", allPayLogisticsMerchantOrderNo );
        params.put("TimeStamp", String.valueOf(System.currentTimeMillis() / 1000));

        String encrypt = encryptAES(params);
        String hashData = compressureSHA(encrypt) ;

        Map<String , String> formData = new HashMap<>();
        formData.put("UID_" , merchantId);
        formData.put("EncryptData_" , encrypt);
        formData.put("HashData_" , hashData);
        formData.put("Version_" , "1.0");
        formData.put("RespondType_" , "JSON");

        StoreMapResponseDto response = new StoreMapResponseDto();
        response.setActionUrl(PRINT_LABEL_API);

        System.out.println(formData);
        response.setFormData(formData);

        return response;
    }

    public LogisticsStatusQueryDto queryShipping(String queryNo) throws JsonProcessingException {
        String timeStamp = String.valueOf(System.currentTimeMillis()/1000);

        Map<String , Object >  params = new HashMap<>() ;
        params.put("MerchantOrderNo", queryNo);
        params.put("TimeStamp", timeStamp);

        String encryptData = encryptAES(params);
        String hashData = compressureSHA(encryptData);

        Map<String, String> formData = new HashMap<>();
        formData.put("UID_", merchantId);
        formData.put("EncryptData_", encryptData);
        formData.put("HashData_", hashData);
        formData.put("Version_", "1.0");
        formData.put("RespondType_", "JSON");

        String response = sendPostRequest(QUERY_LOGISTICS_API, formData);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.readValue(response, Map.class);

        String status = (String) result.get("Status");
        if (!"SUCCESS".equals(status)) {
            String message = (String) result.get("Message");
            throw new RuntimeException("查詢失敗: " + message);
        }

        // 解密回傳的資料
        String encryptedResult = (String) result.get("EncryptData");
        String decryptedData = decryptAES(encryptedResult);
        log.info("解密後資料: {}", decryptedData);

        JSONObject jsonResponse = new JSONObject(decryptedData);
        LogisticsStatusQueryDto queryDto = new LogisticsStatusQueryDto() ;
        queryDto.setMerchantOrderNo(jsonResponse.optString("MerchantOrderNo"));
        queryDto.setRetId(jsonResponse.optString("RetId"));
        queryDto.setRetString(jsonResponse.optString("RetString"));
        log.info("商店訂單編號 :{} , 貨態代碼 : {}  , 貨態說明 : {}" ,
                queryDto.getMerchantOrderNo() , queryDto.getRetId() , queryDto.getRetString());

        return queryDto ;
    }

    private String sendPostRequest(String url, Map<String, String> formData) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        headers.set("Accept", "application/json, text/plain, */*");
        headers.set("Accept-Language", "zh-TW,zh;q=0.9,en;q=0.8");
        headers.set("Cache-Control", "no-cache");
        headers.set("Connection", "keep-alive");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        formData.forEach(map::add);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("API 請求失敗: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    public String encryptAES(Map<String, Object> params) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(params);

            return AesUtil.encrypt(json, hashKey, hashIv);
        } catch (Exception e) {
            throw new RuntimeException("encryptAES error", e);
        }
    }

    public String decryptAES(String params) {
        try {
            return AesUtil.decryptLogistics(params, hashKey, hashIv);
        } catch (Exception e) {
            throw new RuntimeException("decryptAES error", e);
        }
    }

    private String compressureSHA(String encryptData) {
        String rawData = "HashKey=" + hashKey +
                "&" + encryptData +
                "&HashIV=" + hashIv;

        return DigestUtils.sha256Hex(rawData).toUpperCase();
    }
}
