package com.shop.entity;

import com.shop.dto.CartProduct;
import com.shop.dto.ProductDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Cart implements Serializable {

    //id , format , price ,quantity , price mount
    private int sellerId;

    private List<CartProduct> sellerCart;


}
