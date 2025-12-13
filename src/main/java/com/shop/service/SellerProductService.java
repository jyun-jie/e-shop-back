package com.shop.service;

import com.shop.entity.Product;
import com.shop.entity.ProductPage;

public interface SellerProductService {

    int insertProduct(Product product);

    Product findProdcutById(int id );

    int updateProductById(int id ,Product newProduct);

    int deleteProductById(int id);

    ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize);
}
