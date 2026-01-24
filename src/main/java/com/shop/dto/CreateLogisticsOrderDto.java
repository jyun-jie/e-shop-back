package com.shop.dto;

import lombok.Data;

@Data
public class CreateLogisticsOrderDto {
    private Integer orderId;
    private Integer masterOrderId;
    private String merchantOrderNo;
    private String deliveryType;

    private String storeType;
    private String storeId;
    private String storeName;

    private Integer sellerId ;
    private String senderName;
    private String senderPhone;
    private String senderCellPhone;

    private Integer buyerId ;
    private String receiverName;
    private String receiverPhone;
    private String receiverCellPhone;
    private String receiverEmail;

    private Integer goodsAmount;
    private Boolean isCod;
    private Integer amount;
}
