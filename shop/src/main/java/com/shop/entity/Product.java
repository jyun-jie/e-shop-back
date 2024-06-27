package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

//負責產品的資訊
@Data
@Table(name = "product")
public class Product {
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
