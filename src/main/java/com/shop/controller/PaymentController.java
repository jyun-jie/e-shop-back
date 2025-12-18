package com.shop.controller;

import com.shop.entity.CreatePaymentReq;
import com.shop.entity.Payment;
import com.shop.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/Api/Payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PreAuthorize("hasRole('User')")
    @PostMapping(value = "/newebpay")
    @ResponseBody
    public String pay(@RequestBody Integer req) {
        System.out.println(req.toString());
        String form = paymentService.createPayment(req);
        return form;
    }

    @PreAuthorize("hasRole('User')")
    @PostMapping("/notify")
    public String notify(@RequestParam Map<String,String> data) {
        paymentService.handleNewebPayCallback(data);
        return "OK";
    }

    @PreAuthorize("hasRole('User')")
    @PostMapping("/queryTrade")
    public String queryTradeInfo(@RequestBody Map<String, String> data) {
        String form = paymentService.queryTradeInfo(data);
        return form;

    }
}
