package com.shop.service.serviceimpl;


import ch.qos.logback.core.joran.conditional.IfAction;
import com.shop.dto.ProductDto;
import com.shop.entity.ProductPage;
import com.shop.entity.Product;
import com.shop.mapper.SellerProductMapper;
import com.shop.service.SellerProductService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerProductServiceImpl implements SellerProductService {
    @Autowired
    SellerProductMapper sellerProductMapper;

    @Autowired
    UserService userService;


    @Override
    public  int insertProduct(Product product) {
        int sellerId = userService.findIdbyName();
        int insertResult = insertProductIntoDB(sellerId , product);
        return insertResult;
    }

    public int insertProductIntoDB(int sellerId,Product product){
        return sellerProductMapper.insertProduct(sellerId , product);
    };

    public ProductDto findProdcutById(int id) {
        ProductDto product = sellerProductMapper.selectProductById(id);
        return product ;
    }

    public int updateProductById(int id , Product newProduct){
        //updateResult > 0 represent success
        int updateResult = sellerProductMapper.updateProduct(id , newProduct);
        return updateResult  ;
    }

    //刪除商品
    public int deleteProductById(int id) {
        int deleteResult = sellerProductMapper.deleteProduct(id);
        return deleteResult;
    }

    @Override
    public ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize) {
        int sellerId = userService.findIdbyName();
        List<Product> productList = selectProductPage(pageNum,pageSize,sellerId);
        ProductPage<Product> productPage = new ProductPage<>(pageNum+pageSize,productList);
        return productPage;
    }

    public List<Product> selectProductPage(Integer pageNum, Integer pageSize,int sellerId){
        return sellerProductMapper.selectProductPageBySellerId(pageNum,pageSize,sellerId);
    }

}
