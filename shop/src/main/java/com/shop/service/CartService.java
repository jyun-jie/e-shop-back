package com.shop.service;

import com.shop.entity.Cart;

import java.util.List;

public interface CartService {

    List insertProductToCart(int id , int quantity);

    Object findCartListByUser();
}
