package com.shop.dto;

import lombok.Data;

import java.util.Map;

@Data
public class StoreMapResponseDto {
    private String actionUrl;
    private Map<String, String> formData;
}