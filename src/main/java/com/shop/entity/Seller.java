package com.shop.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "seller")
public class Seller {
    private int id ;
    private int userId ;
    private String shop_name;
    private String shop_logo;
    private LocalDateTime created_at ;
    @Enumerated(EnumType.STRING)
    private SellerStatus status;

}
