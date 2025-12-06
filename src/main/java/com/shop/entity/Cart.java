package com.shop.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Cart implements Serializable {
    private int sellerId;
    private List<CartProduct> cartProductList;
    private double total;
    private String receiverAddress;
    private String payment_method;


}
