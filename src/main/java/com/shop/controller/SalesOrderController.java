package com.shop.controller;

import com.shop.dto.SalesOrderDto;
import com.shop.dto.SentShipOrderDto;
import com.shop.entity.Result;
import com.shop.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/SalesOrder")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;



    //no is pay
    @PreAuthorize("hasRole('User')")
    @RequestMapping(method = RequestMethod.GET , value = "check")
    public Result getSalesOrderByState(@RequestParam String orderState){
        List<SalesOrderDto> mySellOrder= salesOrderService.getSalesOrderByState(orderState);
        return Result.success(mySellOrder);
    }

    /***改put***/
    @PreAuthorize("hasRole('User')")
    @RequestMapping(method = RequestMethod.POST , value = "shipOrder")
    public Result sentShippedOrder(@RequestBody SentShipOrderDto shippedOrderIds){
        boolean isSent =  salesOrderService.sentShippedOrders(shippedOrderIds);
        if(isSent){
            return Result.success();
        }
        return Result.error("有錯誤 請重新送出");
    }

}
