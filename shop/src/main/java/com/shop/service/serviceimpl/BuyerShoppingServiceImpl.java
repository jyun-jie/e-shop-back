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
    private BuyerShoppingMapper shoppingMapper;

    @Override
    public ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize) {
        List<Product> productList = selectProductPage(pageNum,pageSize);
        ProductPage<Product> productPage = new ProductPage<>(pageNum+pageSize,productList);
        return productPage;
    }

    public List<Product> selectProductPage(Integer pageNum, Integer pageSize){
        return shoppingMapper.selectProductPage(pageNum,pageSize);
    }

    @Override
    public ProductDto findProductById(int id) {
        ProductDto product = shoppingMapper.selectProductById(id);
        return product ;
    }
}
