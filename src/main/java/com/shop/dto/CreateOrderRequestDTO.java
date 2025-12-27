package com.shop.dto;

import com.shop.entity.Cart;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDTO {
    private List<Cart> cartList;
    private String payment_method;
    private String receiverAddress;

}
