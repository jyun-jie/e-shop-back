package com.shop.service;

import com.shop.dto.ProductDto;
import com.shop.entity.ProductPage;
import com.shop.entity.Product;

public interface SellerProductService {
    //新增商品
    int insertPro(Product product);

    //獲取某個商品的詳細資料
    ProductDto findProById(int id );

    //更新商品
    int updatePro(int id , Product product);

    //刪除商品
    int deletePro(int id);

    //查看商品 分頁查看
    ProductPage<Product> loadPro(Integer pageNum, Integer pageSize);
}
