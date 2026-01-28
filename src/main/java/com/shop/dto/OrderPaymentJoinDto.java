package com.shop.dto;

import com.shop.entity.OrderState;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class OrderPaymentJoinDto {
    private int id ;
    @Enumerated(EnumType.STRING)
    private OrderState state ;
    private int sellerId ;
    private String receiverName ;
    private String deliveryType ;
    private String postalName ;
    private String pickupStoreId ;
    private String pickupStoreName ;
    private double total ;
    private String tradeNo ;

}
