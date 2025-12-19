package com.shop.service.serviceImpl;


import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import com.shop.mapper.SellerProductMapper;
import com.shop.service.SellerProductService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SellerProductServiceImpl implements SellerProductService {
    @Autowired
    SellerProductMapper sellerProductMapper;
    @Autowired
    UserService userService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public  int insertProduct(Product product) {
        int sellerId = userService.findIdbyName();
        return  sellerProductMapper.insertProduct(sellerId , product);
    }


    public Product findProdcutById(int id) {
        return  sellerProductMapper.selectProductById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateProductById(int id , Product newProduct){
        //updateResult > 0 represent success
        return sellerProductMapper.updateProduct(id , newProduct);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteProductById(int id) {
        /***
         * (急做)同步刪除或停用消費者的商品 ( 防同步問題
        ***/
         return sellerProductMapper.deleteProduct(id);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize) {
        int sellerId = userService.findIdbyName();
        List<Product> productList = sellerProductMapper.selectProductPageBySellerId(pageNum,pageSize,sellerId);
        int newPage = pageNum+pageSize;
        return new ProductPage<>(newPage ,productList);
    }

}
