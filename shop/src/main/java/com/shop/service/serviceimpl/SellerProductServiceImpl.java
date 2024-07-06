package com.shop.service.serviceimpl;


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


    //新增商品
    @Override
    public int insertProduct(Product product) {
        int sellerId = userService.findIdbyName();
        int insertProduct = sellerProductMapper.insertProduct(sellerId , product);
        return insertProduct;
    }

    //獲取某商品的資料
    public ProductDto findProdcutById(int id) {
        ProductDto product = sellerProductMapper.selectProductById(id);
        return product ;
    }

    //更新某商品資料
    public int updateProductById(int id , Product newProduct){
        int updateProduct = sellerProductMapper.updateProduct(id , newProduct);
        return updateProduct ;
    }

    //刪除商品
    public int deleteProductById(int id) {
        int deleteProduct = sellerProductMapper.deleteProduct(id);
        //如果有成功放入sql
        return deleteProduct;
    }

    //分頁查看商品
    @Override
    public ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize) {
        ProductPage<Product> productPage = new ProductPage();
        int sellerId = userService.findIdbyName();
        List<Product> productList = sellerProductMapper.selectProductPageBySellerId(pageNum,pageSize,sellerId);
        //獲取pagehelper得到的當前紀錄，當前頁數據
        int offset = pageNum + productList.size();
        productPage.setPageNum(offset);
        productPage.setProduct(productList);

        return productPage;
    }

}
