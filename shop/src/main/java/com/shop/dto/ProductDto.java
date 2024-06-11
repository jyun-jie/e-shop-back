package com.shop.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ProductDto {
    @TableId(type = IdType.AUTO)
    private int id;

    private String name;
    private String type;
    private String description;
    private String imageUrl;
    private String address;
    private double price;
    private int quantity;
    private int sellerId;

    private double rate;

}