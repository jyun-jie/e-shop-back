package com.shop.dto;

import lombok.Data;

@Data
public class HomeProductDto {
    private int id;
    private String name;
    private int price;
    private String imageUrl;
    private double rate ;
    private String address;
}
