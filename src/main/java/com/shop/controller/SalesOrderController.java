package com.shop.controller;

import com.shop.dto.SalesOrderDto;
import com.shop.dto.SentShipOrderDto;
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

    //獲取訂單(未出貨含未收款)
    @RequestMapping(method = RequestMethod.GET , value = "check")
    public Result getSalesOrderByState(@RequestParam String orderState){
        List<SalesOrderDto> mySellOrder= salesOrderService.getSalesOrderByState(orderState);
        return Result.success(mySellOrder);
    }

    //轉至已出貨列表
    @RequestMapping(method = RequestMethod.POST , value = "shipOrder")
    public Result sentShippedOrder(@RequestBody SentShipOrderDto shippedOrderIds){
        boolean isSent =  salesOrderService.sentShippedOrders(shippedOrderIds);
        if(isSent){
            return Result.success();
        }
        return Result.error("有錯誤 請重新送出");
    }

}
