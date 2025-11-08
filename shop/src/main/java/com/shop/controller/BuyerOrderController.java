package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartProduct;
import com.shop.entity.Result;
import com.shop.service.BuyerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/Order")
public class BuyerOrderController {
    @Autowired
    private BuyerOrderService buyerOrderService;

    @RequestMapping(method = RequestMethod.POST, value = "check")
    public Result generateConfirmOrder(@RequestBody List<CartProduct> productList){
        List<Cart> order = buyerOrderService.generateCheckedOrder(productList);
        return Result.success(order);
    }

    @RequestMapping(method = RequestMethod.POST , value = "order")
    public Result placeOrder(@RequestBody List<Cart> cartList){
        boolean istrue = buyerOrderService.insertOrderList(cartList);
        if(istrue){
            return Result.success();
        }
        return Result.error("商品已售空");
    }

    @RequestMapping(method = RequestMethod.GET , value = "/purchase/")
    public Result getPurchase(@RequestParam String type){
        List<OrderDto> purchaseList = buyerOrderService.getUserOrderByState(type);
        if(purchaseList != null){
            return Result.success(purchaseList);
        }
        return Result.error("查看失敗");
    }

    //確認收貨
    @RequestMapping(method = RequestMethod.PUT , value = "/received")
    public Result confirmComplete(@RequestBody int orderId){
        buyerOrderService.changeStateToReceivedOrCompleted(orderId);
        return Result.success();
    }

}
