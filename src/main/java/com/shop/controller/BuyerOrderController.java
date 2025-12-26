package com.shop.controller;

import com.shop.dto.BuyerOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartProduct;
import com.shop.entity.Result;
import com.shop.service.BuyerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/Order")
public class BuyerOrderController {
    @Autowired
    private BuyerOrderService buyerOrderService;

    @PreAuthorize("hasRole('Buyer')")
    @RequestMapping(method = RequestMethod.POST, value = "check")
    public Result generateConfirmedOrder(@RequestBody List<CartProduct> productList){
        List<Cart> order = buyerOrderService.generateCheckedOrder(productList);
        return Result.success(order);
    }

    @PreAuthorize("hasRole('Buyer')")
    @RequestMapping(method = RequestMethod.POST , value = "order")
    public Result placeOrder(@RequestBody List<Cart> cartList){
        try {
            int masterOrderId = buyerOrderService.insertOrderList(cartList);
            return Result.success(masterOrderId);
        } catch (Exception e) {
            // 這裡會抓到「庫存不足」或其他錯誤
            return Result.error("建立失敗: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('Buyer')")
    @RequestMapping(method = RequestMethod.GET , value = "/State/")
    public Result getPurchase(@RequestParam String type){
        List<OrderDto> purchaseList = buyerOrderService.getUserOrderByState(type);
        if(purchaseList != null){
            return Result.success(purchaseList);
        }
        return Result.error("查看失敗");
    }

    //no is pay
    @PreAuthorize("hasRole('Buyer')")
    @RequestMapping(method = RequestMethod.PUT , value = "/received")
    public Result pickupOrder(@RequestBody BuyerOrderDto pickupOrderList){
        buyerOrderService.changeStateToCompleted(pickupOrderList);
        return Result.success();
    }

}
