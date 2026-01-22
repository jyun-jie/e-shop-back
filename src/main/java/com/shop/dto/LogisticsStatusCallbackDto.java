package com.shop.dto;

import lombok.Data;

/**
 * 物流状态通知DTO（NPA-B58）
 */
@Data
public class LogisticsStatusCallbackDto {

    private String merchantID;
    private String amt;
    private String tradeNo;
    private String merchantOrderNo;
    private String respondType;
    private String checkCode;
    private String iP;
    private String escrowBank;
    private String paymentType;
    private String payTime;
    private String storeCode;
    private String storeType;
    private String storeName;
    private String tradeType;
    private String storeAddr;
    private String CVSCOMName;
    private String CVSCOMPhone;


}
