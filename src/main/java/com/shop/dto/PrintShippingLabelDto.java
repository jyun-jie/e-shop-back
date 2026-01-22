package com.shop.dto;

import lombok.Data;
import java.util.List;

/**
 * 列印寄货单请求DTO（NPA-B54）
 */
@Data
public class PrintShippingLabelDto {
    private List<Integer> logisticsOrderIds;  // 物流订单ID列表
    private String storeType;                  // 超商类型
}
