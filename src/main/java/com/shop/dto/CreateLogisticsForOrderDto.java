package com.shop.dto;

import lombok.Data;

import java.util.List;

/**
 * 为订单创建物流单的请求DTO
 * 卖家在准备出货时使用
 */
@Data
public class CreateLogisticsForOrderDto {
    private List<Integer> checkOrderList;               // 订单ID
}
