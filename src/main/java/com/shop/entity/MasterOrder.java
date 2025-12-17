package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "master_order")
public class MasterOrder {
    @TableId(type = IdType.AUTO)
    private int id;   //PK
    private int buyer_id;
    private int total_amount;     //  所有子訂單加總
    private String payment_status ; //  UNPAID / PAID
    private String pay_method  ; //   CREDIT_CARD / Cash on Delivery
    private LocalDateTime created_at ;
}
