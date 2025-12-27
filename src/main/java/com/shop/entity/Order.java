package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "order")
public class Order {
    @TableId(type = IdType.AUTO)
    private int id;
    private int master_order_id ;
    private int userId;
    private int sellerId;
    @Enumerated(EnumType.STRING)
    private OrderState state;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
    private double total;
    private String postalAddress;
    private String receiverAddress;
    private String postalName;
    private String receiverName;



}
