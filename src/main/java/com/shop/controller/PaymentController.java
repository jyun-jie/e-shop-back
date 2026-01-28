package com.shop.controller;

import com.shop.entity.CreatePaymentReq;
import com.shop.entity.Payment;
import com.shop.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/Api/Payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${newebpay.logistics-get-paymentResult}")
    private String logisticsGetPaymentResultUrl;

    @PreAuthorize("hasRole('User')")
    @PostMapping(value = "/newebpay", consumes = "application/json",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String pay(@RequestBody Integer req) {
        log.info("pay {} " , req.toString());
        String form = paymentService.createPayment(req);
        return form;
    }


    @PostMapping("/notify")
    public String notify(@RequestParam Map<String,String> data) {
        paymentService.handleNewebPayCallback(data);
        log.info("notify");
        return "OK";
    }

    @PreAuthorize("hasRole('User')")
    @PostMapping("/queryTrade")
    public String queryTradeInfo(@RequestBody Map<String, String> data) {
        String form = paymentService.queryTradeInfo(data);
        return form;
    }


    @PreAuthorize("hasRole('User')")
    @PostMapping("/changePaid")
    public void changePayStatus(@RequestParam String data) {
        paymentService.changePayStatus(data);
        log.info("MerchatId : "+data + ", CodChangeStatus");
    }

    @RequestMapping(
            value = "/result",
            method = {RequestMethod.POST, RequestMethod.GET},
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public void getPaymentResult(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {
        Map<String, String[]> rawParams = request.getParameterMap();
        log.info("raw param : {}", rawParams.get("Status"));

        String status = request.getParameter("Status");
        String message = request.getParameter("Message");

        StringBuilder targetUrl = new StringBuilder(logisticsGetPaymentResultUrl);
        targetUrl.append("?status=").append(status);

        if (!"SUCCESS".equals(status) && message != null) {
            targetUrl.append("&msg=").append(URLEncoder.encode(message, StandardCharsets.UTF_8));
        }

        response.sendRedirect(targetUrl.toString());

    }
}
