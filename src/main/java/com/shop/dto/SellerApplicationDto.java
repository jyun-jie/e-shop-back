package com.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerApplicationDto {
    private String shop_name ;
    private String card_number ;
    private String bank_account ;
}
