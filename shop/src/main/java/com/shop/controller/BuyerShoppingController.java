package com.shop.controller;

import com.shop.dto.ProductDto;
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

    //查詢所有商品 (分頁)
    @RequestMapping(method = RequestMethod.GET , value = "/unAuth/Pro")
    public Result getProductPage(Integer pageNum, Integer pageSize){
        ProductPage productPage = ShoppingService.findProductPage(pageNum,pageSize);
        if(productPage != null){
            return Result.success(productPage);
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }

    //進到產品詳情
    @RequestMapping(method = RequestMethod.GET ,value = "/unAuth/Pro/{id}")
    //獲取某單一商品訊息
    public Result selectProDetl(@PathVariable int id){
        ProductDto product = ShoppingService.findProductById(id);
        if(product != null){
            return Result.success(product);
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }

    //
}
