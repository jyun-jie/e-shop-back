package com.shop.service;

import com.shop.entity.Cart;

import java.util.List;

public interface CartService {


    //新增商品至購物車
    List addToCart(int id , int quantity);

    Object findUserCart();
}
