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

    @RequestMapping(method = RequestMethod.POST , value = "/add/{id}/{quantity}")
    public Result insertProductToCart(@PathVariable int id,@PathVariable int quantity){
        List cartList = cartService.insertProductToCart(id,quantity);
        if(cartList == null){
            return Result.error("空的");
        }else{
            return Result.success("成功加入");
        }
    }

    @RequestMapping(method = RequestMethod.GET , value = "/findCartList")
    public Result getCartList(){
        List cartList =(List<Cart>) cartService.findCartListByUser();
        return Result.success(cartList);
    }
}
