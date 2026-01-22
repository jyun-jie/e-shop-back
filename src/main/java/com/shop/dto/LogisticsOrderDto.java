package com.shop.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 物流订单DTO（用于返回给前端）
 */
@Data
public class LogisticsOrderDto {
    private Integer id;
    private Integer orderId;
    private Integer masterOrderId;
    private String logisticsType;
    private String storeType;
    private String allPayLogisticsId;
    private String storeName;
    private String logisticsStatus;
    private String logisticsStatusDesc;
    private Boolean isCod;
    private Integer codAmount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
