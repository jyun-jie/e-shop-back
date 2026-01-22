package com.shop.dto;

import lombok.Data;

/**
 * 门市信息DTO
 */
@Data
public class StoreInfoDto {
    private String storeId;        // 门市代码
    private String storeName;      // 门市名称
    private String storeAddress;   // 门市地址
    private String storeType;      // 超商类型
    private String LgsType;
}
