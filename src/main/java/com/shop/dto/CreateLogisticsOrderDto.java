package com.shop.dto;

import lombok.Data;

/**
 * 建立物流寄货单请求DTO（NPA-B52）
 */
@Data
public class CreateLogisticsOrderDto {
    private Integer orderId;                   // 订单ID
    private Integer masterOrderId;             // 主订单ID
    private String merchantOrderNo;            // 商户订单号
    private String deliveryType;              // C2C / B2C

    private String storeType;                  // 7-ELEVEN / FAMILY / HILIFE / OK
    private String storeId;                    // 取货门市代码
    private String storeName;                  // 取货门市名称

    private Integer sellerId ;
    private String senderName;
    private String senderPhone;
    private String senderCellPhone;

    private Integer buyerId ;
    private String receiverName;
    private String receiverPhone;
    private String receiverCellPhone;
    private String receiverEmail;

    private Integer goodsAmount;               // 商品金额
    private Boolean isCod;                     // 是否取货付款
    private Integer codAmount;                 // 取货付款金额（如果是COD）
}
