package com.shop.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "seller_Application")
public class SellerApplication {
    @Id
    private int id ;
    private int userId ;
    private String shop_name ;
    private String card_number ;
    private String bank_account ;
    @Enumerated(EnumType.STRING)
    private sellerApplicationStatus status ;
    private LocalDateTime applied_at ;
    private LocalDateTime review_at ;
    private int review_by ;
    private String reject_reason ;

}
