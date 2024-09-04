package com.shop.service.serviceimpl;


import com.shop.entity.Product;
import com.shop.entity.ProductPage;
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
        return  sellerProductMapper.selectProductById(id);
    }
    public int updateProductById(int id , Product newProduct){
        //updateResult > 0 represent success
        return sellerProductMapper.updateProduct(id , newProduct);
    }
    public int deleteProductById(int id) {
        return sellerProductMapper.deleteProduct(id);
    }
    @Override
    public ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize) {
        int sellerId = userService.findIdbyName();
        List<Product> productList = sellerProductMapper.selectProductPageBySellerId(pageNum,pageSize,sellerId);
        int newPage = pageNum+pageSize;
        return new ProductPage<>(newPage ,productList);
    }

}
