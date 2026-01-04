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
                 /**
                  * 先預設為現在(做測試)
                  * **/
                 LocalDateTime.now() , //.minusDays(7)
                 LocalDateTime.now().minusDays(37)
         );

         for(Order order : payoutOrder) {

             List<Integer> wasCalculatedPayout = sellerMapper.getWasCalculatedPayout(LocalDateTime.now().minusDays(37)) ;

             if(wasCalculatedPayout.contains((Integer) order.getId())){
                 continue;
             }

             PayoutDto payout = new PayoutDto();
             payout.setSellerId(order.getSellerId());
             payout.setOrderId(order.getId());
             payout.setAmount(order.getTotal());
             payout.setPayoutStatus(PayoutStatus.pending);
             payout.setAvailable_at(LocalDateTime.now());

             int sellerPayoutId =  sellerMapper.insertMonthPayout(payout) ;
             log.info("{} 以插入本月該付款名單" , sellerPayoutId);
         }

    }

}
