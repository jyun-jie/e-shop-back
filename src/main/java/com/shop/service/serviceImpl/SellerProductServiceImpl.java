package com.shop.service.serviceImpl;


import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import com.shop.mapper.SellerProductMapper;
import com.shop.service.SellerProductService;
import com.shop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
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
        log.info("開始嘗試修改商品 ID: {}", id);
        Product product= sellerProductMapper.selectProductForUpdate(id);

        if (product == null) {
            log.warn("商品 {} 不存在", id);
            throw new NoSuchElementException("找不到該商品，無法執行刪除");
        }

        int result = sellerProductMapper.updateProduct(id , newProduct);
        if (result == 0) {
            throw new RuntimeException("修改商品失敗，請稍後再試");
        }

        log.info("商品 {} 邏輯刪除成功", id);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteProductById(int id) {
        log.info("開始嘗試刪除商品 ID: {}", id);
        // use FOR UPDATE to lock the data in the column
        Product product= sellerProductMapper.selectProductForUpdate(id);

        if (product == null) {
            log.warn("商品 {} 不存在", id);
            throw new NoSuchElementException("找不到該商品，無法執行刪除");
        }

        if (product.getStatus() == "take_down") {
            log.info("商品 {} 已經是刪除狀態，無需重複執行", id);
            throw new RuntimeException("找不到該商品，無法執行刪除");
        }

        int result = sellerProductMapper.logicDeleteProduct(id);
        if (result == 0) {
            throw new RuntimeException("刪除商品失敗，請稍後再試");
        }

        log.info("商品 {} 邏輯刪除成功", id);
        return result;
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
