package com.shop.service.scheduler;

import com.shop.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PayoutScheduleService {

    @Autowired
    private AdminService adminService;

    @Scheduled(cron = "0 * * * * ?", zone = "Asia/Taipei")
    public void generatePayout() {
        log.info("開始計算賣家放款...");
        adminService.generateRoutinePayout();
        log.info("賣家放款計算完成");
    }
}
