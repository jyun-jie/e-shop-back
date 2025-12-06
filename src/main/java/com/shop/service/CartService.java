package com.shop.service;

import java.util.List;

public interface CartService {

    List insertProductToCart(int id , int quantity);

    Object findCartListByUser();
}
