package com.shop.dto;

import com.shop.entity.Cart;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDTO {
    private List<Cart> cartList;
    private String payment_method;
    private String receiverPhone ;
    private String receiverEmail;
    
    // 物流相关字段
    private String deliveryType;          // HOME_DELIVERY / STORE_PICKUP
    private String pickupStoreType;       // 1(7-ELEVEN) / 2(FAMILY) / 3(HILIFE) / 4(OK)
    private String pickupStoreId;
    private String pickupStoreName;
    private String pickupStoreAddress;
}
