package com.shop.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartProduct implements Serializable {
    private int id;
    private String name;
    private double price;
    private int quantity;

}
