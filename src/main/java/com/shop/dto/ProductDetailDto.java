package com.shop.dto;

import lombok.Data;

@Data
public class ProductDetailDto {
    private String name;
    private String type ;
    private int price;
    private int quantity;
    private Integer imageId;
    private String imageUrl;
    private String address;
    private String description;
}
