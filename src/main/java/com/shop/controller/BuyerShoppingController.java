package com.shop.controller;

import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import com.shop.entity.Result;
import com.shop.service.BuyerShoppingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/Read")
public class BuyerShoppingController {
    @Autowired
    private BuyerShoppingService ShoppingService;


    @RequestMapping(method = RequestMethod.GET , value = "/unAuth/Pro")
    public Result getProductPage(Integer pageNum, Integer pageSize){
        log.info("pagenum : {} pagesize: {} " , pageNum  , pageSize);
        ProductPage productPage = ShoppingService.findProductPage(pageNum,pageSize);
        if(productPage.getProductList() != null){
            return Result.success(productPage);
        }
        return Result.error("失敗 請再次嘗試");

    }

    @PreAuthorize("hasRole('Buyer')")
    @RequestMapping(method = RequestMethod.GET ,value = "/Pro/{id}")
    public Result getProductDetail(@PathVariable int id){
        Product product = ShoppingService.findProductById(id);
        if(product != null){
            return Result.success(product);
        }
        return Result.error("失敗 請再次嘗試");

    }
}
