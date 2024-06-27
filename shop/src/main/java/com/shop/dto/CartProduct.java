package com.shop.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class CartProduct implements Serializable {
    private int id;
    private String name;
    private double price;
    private int quantity;

    public CartProduct(int id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
