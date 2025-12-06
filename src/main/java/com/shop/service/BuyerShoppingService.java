package com.shop.service;

import com.shop.entity.Product;
import com.shop.entity.ProductPage;

public interface BuyerShoppingService {

    public ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize);


}
