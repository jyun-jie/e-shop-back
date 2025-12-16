package com.shop.controller;

import com.shop.entity.CreatePaymentReq;
import com.shop.entity.Payment;
import com.shop.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/Api/Payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @PostMapping(value = "/newebpay", consumes = "application/json",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String pay(@RequestBody Integer req) {
        String form = paymentService.createPayment(req);
        return form;
    }

    @PostMapping("/notify")
    public String notify(@RequestParam Map<String, String> data) {
        paymentService.handleNewebPayCallback(data);
        return "OK";
    }
}
