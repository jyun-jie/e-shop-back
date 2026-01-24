package com.shop.service;

import com.shop.entity.Payment;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


public interface PaymentService {

    String createPayment(int payment);

    void handleNewebPayCallback(Map<String, String> data);

    String queryTradeInfo(Map<String, String> data);

    void changePayStatus(String data);
    void getPaymentResult(Map<String, String> params , HttpServletResponse response)throws IOException;
}
