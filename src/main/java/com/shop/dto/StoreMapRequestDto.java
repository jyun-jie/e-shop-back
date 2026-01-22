package com.shop.dto;

import lombok.Data;

/**
 * 门市地图查询请求DTO（NPA-B51）
 */
@Data
public class StoreMapRequestDto {
    private String orderNo;      // 7-ELEVEN / FAMILY / HILIFE / OK
    private String lgsType;           // 城市（可选）
    private String shipType;       // 地址（可选，用于查询附近门市）

}
