package com.shop.service;

import com.shop.dto.ProductDto;
import com.shop.entity.ProPage;
import com.shop.entity.Product;

public interface ReadProductService {

    public ProPage<Product> loadPro(Integer pageNum, Integer pageSize);

    public ProductDto findProById (int id);
}
