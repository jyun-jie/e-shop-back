package com.shop.service;

import com.baomidou.mybatisplus.core.injector.methods.SelectList;
import com.shop.dto.ProductDto;
import com.shop.entity.ProPage;
import com.shop.entity.Product;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SellProductService {
    //新增商品
    int insertPro(Product product);

    //獲取某個商品的詳細資料
    ProductDto findProById(int id );

    //更新商品
    int updatePro(int id , Product product);

    //刪除商品
    int deletePro(int id);

    //查看商品 分頁查看
    ProPage<Product> loadPro(Integer pageNum, Integer pageSize);
}
