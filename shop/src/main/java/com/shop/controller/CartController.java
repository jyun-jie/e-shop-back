package com.shop.controller;


import com.google.gson.Gson;
import com.shop.entity.Cart;
import com.shop.entity.Product;
import com.shop.entity.Result;
import com.shop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/Cart")
public class CartController {

    @Autowired
    private CartService cartService;





    //添加至購物車
    @RequestMapping(method = RequestMethod.POST , value = "/add/{id}/{quantity}")
    public Result addToCart(@PathVariable int id,@PathVariable int quantity){
        List i = cartService.addToCart(id,quantity);
        return Result.success(i);
    }

    //購物車頁面一加載，將所有購物車數據傳到前端
    //從redis查詢數據
    @RequestMapping(method = RequestMethod.GET , value = "/findCartList")
    public Result findCartList(){
        //查詢該userId的購物車
        List cartList =(List<Cart>) cartService.findUserCart();
        return Result.success(cartList);
    }
}
