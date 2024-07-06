package com.shop.service.serviceimpl;

import com.shop.dto.ProductDto;
import com.shop.entity.ProductPage;
import com.shop.entity.Product;
import com.shop.mapper.BuyerShoppingMapper;
import com.shop.service.BuyerShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuyerShoppingServiceImpl implements BuyerShoppingService {
    @Autowired
    private BuyerShoppingMapper ShoppingMapper;

    @Override
    public ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize) {
        ProductPage<Product> productPage = new ProductPage();
        //找到userid
        List<Product> productList = ShoppingMapper.selectProductPage(pageNum,pageSize);
        //獲取pagehelper得到的當前紀錄，當前頁數據
        int offset = pageNum + productList.size();
        productPage.setPageNum(offset);
        productPage.setProduct(productList);
        return productPage;
    }

    //獲取某商品的資料
    @Override
    public ProductDto findProductById(int id) {
        ProductDto product = ShoppingMapper.selectProductById(id);
        //如果有成功放入sql
        return product ;
    }
}
