package com.shop.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDto {

    private int id;
    private String sellerName;
    private List<InOrderProductDto> orderProductList;
    private double total;
}
