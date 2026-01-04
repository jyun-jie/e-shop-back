package com.shop.config;

import com.shop.service.AdminService;
import com.shop.service.serviceImpl.AdminServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
public class SpringConfig {

    @Autowired
    private AdminService adminService ;

    /* 測試以秒為測試*/
    @Scheduled(cron = "0 * * * * ?", zone = "Asia/Taipei")
    public void generatePayout() {
        log.info("開始計算賣家放款...");
        adminService.generateRoutinePayout();
        log.info("賣家放款計算完成");
    }

}
