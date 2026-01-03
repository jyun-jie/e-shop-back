package com.shop.dto;

import com.shop.entity.ImageType;
import lombok.Data;

@Data
public class ProductDetailDto {
    private String name;
    private String type ;
    private int price;
    private int quantity;
    private Integer imageId;
    private String imageUrl;
    private ImageType imageType ;
    private String address;
    private String description;

}
