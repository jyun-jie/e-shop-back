package com.shop.service;

import com.shop.dto.HomeProductDto;
import com.shop.entity.Product;
import com.shop.entity.ProductPage;

public interface BuyerShoppingService {

    public ProductPage<HomeProductDto> findProductPage(Integer pageNum, Integer pageSize);

    public Product findProductById (int id);
}
