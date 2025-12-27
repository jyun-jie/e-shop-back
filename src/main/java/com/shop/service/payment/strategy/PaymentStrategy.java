package com.shop.service.payment.strategy;

import com.shop.entity.MasterOrder;

public interface PaymentStrategy {

    String createPayment(MasterOrder order);
}
