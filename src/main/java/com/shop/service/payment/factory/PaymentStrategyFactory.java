package com.shop.service.payment.factory;

import com.shop.service.payment.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentStrategyFactory {
    private final Map<String, PaymentStrategy> strategies;

    public PaymentStrategyFactory(Map<String, PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public PaymentStrategy getStrategy(String paymentMethod) {
        return strategies.get(paymentMethod);
    }

}
