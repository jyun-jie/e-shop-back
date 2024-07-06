package com.shop.controller;


import com.shop.entity.Cart;
import com.shop.entity.Result;
import com.shop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Result insertProductToCart(@PathVariable int id,@PathVariable int quantity){
        List i = cartService.insertProductToCart(id,quantity);
        return Result.success(i);
    }

    //購物車頁面一加載，將所有購物車數據傳到前端
    //從redis查詢數據
    @RequestMapping(method = RequestMethod.GET , value = "/findCartList")
    public Result getCartList(){
        //查詢該userId的購物車
        List cartList =(List<Cart>) cartService.findCartByUser();
        return Result.success(cartList);
    }
}
