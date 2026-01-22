package com.shop.service.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shop.dto.LogisticsStatusQueryDto;
import com.shop.entity.LogisticsOrder;
import com.shop.mapper.LogisticsMapper;
import com.shop.service.LogisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class LogisticsScheduleService {

    @Autowired
    private LogisticsMapper logisticsMapper;

    @Autowired
    private LogisticsService logisticsService;

    public LogisticsScheduleService(LogisticsMapper logisticsMapper) {
        this.logisticsMapper = logisticsMapper;
    }

    @Scheduled(cron = "20 * * * * ?")
    @Transactional
    public void updateLogisticsOrderStatus() throws JsonProcessingException {
        List<LogisticsOrder> StatusCheckOrders = logisticsMapper.findNeedStatusCheck() ;

        for(LogisticsOrder order : StatusCheckOrders){

            if (order.getUpdateTime() != null &&
                    Duration.between(order.getUpdateTime(), LocalDateTime.now()).toMinutes() < 5) {
                continue;
            }

            LogisticsStatusQueryDto orderStatus = logisticsService.queryShipping(order.getMerchantOrderNo());
            logisticsMapper.updateStatus(orderStatus , false);
            log.info(" 物流訂單編號 : {} 狀態更新為 : {} 狀態說明 : {} " ,
                    orderStatus.getMerchantOrderNo() ,
                    orderStatus.getRetId() ,
                    orderStatus.getRetString() );
        }

    }

}
