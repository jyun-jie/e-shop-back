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
@Table(name ="payment")
public class Payment {
    @TableId(type = IdType.AUTO)
    private int id;
    private int master_order_id ;
    private String trade_no ;
    private int amount ;
    private String pay_status ;
    private LocalDateTime payTime ;


}
