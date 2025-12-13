package com.shop.dto;

import lombok.Data;

@Data
public class InOrderProductDto {
    private  int product_Id;
    private String productName;
    private double price;
    private int quantity;
}
