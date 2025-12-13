package com.shop.service;

import com.shop.dto.BuyerOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartProduct;

import java.util.List;

public interface BuyerOrderService {
    List<Cart> generateCheckedOrder(List<CartProduct> productList);
    Boolean insertOrderList(List<Cart> cartList);

    List<OrderDto> getUserOrderByState(String type);

    void changeStateToCompleted(BuyerOrderDto pickupOrderList);
}
