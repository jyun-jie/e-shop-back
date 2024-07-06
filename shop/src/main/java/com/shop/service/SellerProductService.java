package com.shop.service;

import com.shop.dto.ProductDto;
import com.shop.entity.ProductPage;
import com.shop.entity.Product;

public interface SellerProductService {
    //新增商品
    int insertProduct(Product product);

    //獲取某個商品的詳細資料
    ProductDto findProdcutById(int id );

    //更新商品
    int updateProductById(int id ,Product newProduct);

    //刪除商品
    int deleteProductById(int id);

    //查看商品 分頁查看
    ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize);
}
