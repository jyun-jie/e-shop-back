package com.shop.service;

import com.shop.dto.ProductDto;
import com.shop.entity.ProductPage;
import com.shop.entity.Product;

public interface SellerProductService {

    int insertProduct(Product product);

    Product findProdcutById(int id );

    int updateProductById(int id ,Product newProduct);

    int deleteProductById(int id);

    ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize);
}
