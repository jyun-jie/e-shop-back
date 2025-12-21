package com.shop.service.serviceImpl;

import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import com.shop.mapper.BuyerShoppingMapper;
import com.shop.service.BuyerShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BuyerShoppingServiceImpl implements BuyerShoppingService {
    @Autowired
    private BuyerShoppingMapper shoppingMapper;

    @Transactional(readOnly = true)
    @Override
    public ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize) {
        List<Product> productList = shoppingMapper.selectProductPage(pageNum,pageSize);
        int newPage = pageNum+pageSize;
        return new ProductPage<>(newPage,productList);
    }

    @Transactional(readOnly = true)
    @Override
    public Product findProductById(int id) {
        return shoppingMapper.selectProductById(id);
    }

}
