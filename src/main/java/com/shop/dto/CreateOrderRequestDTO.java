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
    private String deliveryType;
    private String pickupStoreType;
    private String pickupStoreId;
    private String pickupStoreName;
    private String pickupStoreAddress;
}
