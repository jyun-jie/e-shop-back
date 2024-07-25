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
        return  sellerProductMapper.insertProduct(sellerId , product);

    }
    public Product findProdcutById(int id) {
        Product product = sellerProductMapper.selectProductById(id);
        return product ;
    }
    public int updateProductById(int id , Product newProduct){
        //updateResult > 0 represent success
        int isUpdate = sellerProductMapper.updateProduct(id , newProduct);
        return isUpdate  ;
    }
    public int deleteProductById(int id) {
        int isDelete = sellerProductMapper.deleteProduct(id);
        return isDelete;
    }
    @Override
    public ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize) {
        int sellerId = userService.findIdbyName();
        List<Product> productList = sellerProductMapper.selectProductPageBySellerId(pageNum,pageSize,sellerId);
        return new ProductPage<>(pageNum+pageSize,productList);
    }

}
