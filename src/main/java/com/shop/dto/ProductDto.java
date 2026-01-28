package com.shop.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable {
    @TableId(type = IdType.AUTO)
    private int id;
    private String name;
    private String type;
    private double price;
    private int quantity;
    private String description;
    private String address;
    private int sellerId;
    private double rate;

}