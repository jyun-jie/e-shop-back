package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "InOrderProduct")
public class InOrderProduct {
    @TableId(type= IdType.AUTO)
    private int order_contentId;
    private int orderId;
    private int product_Id;
    private String productName;
    private double price;
    private int quantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
