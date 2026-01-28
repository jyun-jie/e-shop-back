package com.shop.dto;

import com.shop.entity.Order;
import com.shop.entity.PayoutStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PayoutDto {

    private int id ;
    private int sellerId ;
    private int orderId ;
    private String orderName ;
    private double amount;
    @Enumerated(EnumType.STRING)
    private PayoutStatus payoutStatus;
    private LocalDateTime available_at ;
    private LocalDateTime paid_at ;
}
