package com.shop.service;

import com.shop.dto.ProductDto;
import com.shop.entity.ProductPage;
import com.shop.entity.Product;

public interface BuyerShoppingService {

    public ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize);

    public ProductDto findProductById (int id);
}
