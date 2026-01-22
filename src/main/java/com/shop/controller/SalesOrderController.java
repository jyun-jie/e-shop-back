package com.shop.controller;

import com.shop.dto.CreateLogisticsForOrderDto;
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
        System.out.println(orderState);
        List<SalesOrderDto> mySellOrder= salesOrderService.getSalesOrderByState("sales" , orderState ,"0" );
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

    @PreAuthorize("hasRole('User')")
    @RequestMapping(method = RequestMethod.GET , value = "NonCreatedLogisticsOrders")
    public Result getNonCreatedLogisticsOrders(@RequestParam String storeType){
        System.out.println(storeType);
        List<SalesOrderDto> orders = salesOrderService.getNonCreatedLogisticsOrders(storeType);
        return Result.success(orders);
    }

    @PreAuthorize("hasRole('User')")
    @PostMapping("/createLogistics")
    public Result createLogisticsForOrder(@RequestBody CreateLogisticsForOrderDto request) {
        try {
            System.out.println("近來");
            String logisticsId = salesOrderService.createLogisticsForOrder(request);
            return Result.success(logisticsId);
        } catch (Exception e) {
            return Result.error("创建物流单失败: " + e.getMessage());
        }
    }

}
