package com.shop.service;

import com.shop.entity.Payment;
import org.springframework.stereotype.Service;

import java.util.Map;


public interface PaymentService {

    String createPayment(int payment);

    void handleNewebPayCallback(Map<String, String> data);
}
