package com.shop.controller;

import com.shop.dto.SalesOrderDto;
import com.shop.entity.OrderState;
import com.shop.entity.Result;
import com.shop.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/SalesOrder")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    //獲取訂單(未出貨)
    @RequestMapping(method = RequestMethod.GET , value = "check")
    public Result getSalesOrder(@RequestParam String orderState){
        System.out.println(orderState);
        List<SalesOrderDto> mySellOrder= salesOrderService.getSalesOrders(orderState);
        return Result.success(mySellOrder);
    }

    //轉至已出貨列表
    @RequestMapping(method = RequestMethod.POST , value = "shipOrder")
    public Result sentShippedOrder(@RequestBody List<Integer> shippedOrderId){
        boolean isSent =  salesOrderService.sentShippedOrders(shippedOrderId);
        if(isSent){
            return Result.success();
        }
        return Result.error("有錯誤 請重新送出");
    }
    //轉至已到貨

    //買家收貨(未支付轉已支付)

    //轉至已完成訂單
}
