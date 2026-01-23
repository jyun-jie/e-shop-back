package com.shop.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateLogisticsForOrderDto {
    private List<Integer> checkOrderList;
}
