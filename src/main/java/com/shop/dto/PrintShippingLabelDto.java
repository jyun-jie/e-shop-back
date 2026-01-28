package com.shop.dto;

import lombok.Data;
import java.util.List;

@Data
public class PrintShippingLabelDto {
    private List<Integer> logisticsOrderIds;
    private String storeType;
}
