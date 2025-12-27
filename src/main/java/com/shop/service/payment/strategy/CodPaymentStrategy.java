package com.shop.service.payment.strategy;

import com.shop.entity.MasterOrder;
import com.shop.entity.Payment;
import com.shop.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("COD")
public class CodPaymentStrategy implements PaymentStrategy {

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public String createPayment(MasterOrder order) {

        String tradeNo = "NP" + System.currentTimeMillis();

        Payment payment = new Payment();
        payment.setMaster_order_id(order.getId());
        payment.setTrade_no(tradeNo);
        payment.setAmount(order.getTotal_amount());
        payment.setPay_status("INIT");

        paymentMapper.insert(payment);

        System.out.println("COD");

        return "COD";
    }
}
