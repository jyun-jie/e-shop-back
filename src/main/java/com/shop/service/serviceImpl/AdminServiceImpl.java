package com.shop.service.serviceImpl;


import com.shop.dto.PayoutDto;
import com.shop.entity.Order;
import com.shop.entity.PayoutStatus;
import com.shop.entity.WasCalculatePayout;
import com.shop.mapper.SalesOrderMapper;
import com.shop.mapper.SellerMapper;
import com.shop.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    @Autowired
    private SellerMapper sellerMapper;



    public void generateRoutinePayout(){
         List<Order> payoutOrder = salesOrderMapper.findCompletedAndNotPayout(
                 LocalDateTime.now()
         );
        System.out.println(payoutOrder);


         for(Order order : payoutOrder) {

             System.out.println(order);

             PayoutDto payout = new PayoutDto();
             payout.setSellerId(order.getSellerId());
             payout.setOrderId(order.getId());
             payout.setAmount(order.getTotal());
             payout.setPayoutStatus(PayoutStatus.pending);
             payout.setAvailable_at(LocalDateTime.now());

             try {
                 sellerMapper.insertMonthPayout(payout);
                 log.info("{} 以插入本月該付款名單" , order.getId());
             } catch (DuplicateKeyException e) {
                 // 防止重複排程 or 多機
                 log.warn("Order {} 已結算，跳過", order.getId());
             }

         }

    }

}
