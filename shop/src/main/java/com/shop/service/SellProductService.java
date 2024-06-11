package com.shop.service;

import com.baomidou.mybatisplus.core.injector.methods.SelectList;
import com.shop.dto.ProductDto;
import com.shop.entity.ProPage;
import com.shop.entity.Product;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SellProductService {

    //獲取自己賣商品的資料
    List selectMyPro();

    //新增商品
    int insertPro(Product product);

    ProductDto findProById(int id );

    int updatePro(int id , Product product);

    int deletePro(int id);

   ProPage<Product> loadPro(Integer pageNum, Integer pageSize);
}
