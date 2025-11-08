package com.shop.controller;

import com.shop.dto.SalesOrderDto;
import com.shop.entity.Result;
import com.shop.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/SalesOrder")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    //獲取訂單(未出貨)
    @RequestMapping(method = RequestMethod.GET , value = "check")
    public Result getSalesOrder(){
        List<SalesOrderDto> mySellOrder= salesOrderService.getSalesOrders();
        return Result.success(mySellOrder);
    }
    //轉至已出貨列表

    //轉至已到貨

    //買家收貨(未支付轉已支付)

    //轉至已完成訂單
}
