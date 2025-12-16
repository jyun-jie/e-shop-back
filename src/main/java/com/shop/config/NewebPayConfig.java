package com.shop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "newebpay")
@Data
public class NewebPayConfig {
    private String merchantId;
    private String hashKey;
    private String hashIv;
    private String notifyUrl;
    private String returnUrl;
}
