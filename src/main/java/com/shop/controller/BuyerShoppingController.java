package com.shop.controller;

import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import com.shop.entity.Result;
import com.shop.service.BuyerShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/Read")
public class BuyerShoppingController {
    @Autowired
    private BuyerShoppingService ShoppingService;

    @RequestMapping(method = RequestMethod.GET , value = "/unAuth/Pro")
    public Result getProductPage(Integer pageNum, Integer pageSize){
        ProductPage productPage = ShoppingService.findProductPage(pageNum,pageSize);
        if(productPage.getProductList() != null){
            return Result.success(productPage);
        }
        return Result.error("失敗 請再次嘗試");

    }

    @RequestMapping(method = RequestMethod.GET ,value = "/unAuth/Pro/{id}")
    public Result getProductDetail(@PathVariable int id){
        Product product = ShoppingService.findProductById(id);
        if(product != null){
            return Result.success(product);
        }
        return Result.error("失敗 請再次嘗試");

    }
}
