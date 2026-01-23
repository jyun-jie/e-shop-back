package com.shop.service.scheduler;

import com.shop.entity.InOrderProduct;
import com.shop.entity.MasterOrder;
import com.shop.entity.Order;
import com.shop.entity.OrderState;
import com.shop.mapper.BuyerOrderMapper;
import com.shop.mapper.MasterOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderTimeoutService {

    @Autowired
    private MasterOrderMapper masterOrderMapper;

    @Autowired
    private BuyerOrderMapper buyerOrderMapper;

    @Scheduled(cron = "0 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void checkTimeoutOrders() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(30);
        List<MasterOrder> expiredOrders = masterOrderMapper.selectExpiredUnpaidOrders(timeoutThreshold);

        if (expiredOrders.isEmpty()) {
            return;
        }

        for (MasterOrder masterOrder : expiredOrders) {
            masterOrderMapper.updateStatus(masterOrder.getId(), "FAILED");
            buyerOrderMapper.updateStateByMasterOrderId(masterOrder.getId(), OrderState.CANCELLED);

            List<Order> subOrders = buyerOrderMapper.selectByMasterOrderId(masterOrder.getId());
            for (Order order : subOrders) {
                List<InOrderProduct> products = buyerOrderMapper.selectInOrderproductByOrderId(order.getId());
                for (InOrderProduct product : products) {
                    buyerOrderMapper.increaseStock(product.getProduct_Id(), product.getQuantity());
                }
            }
            
            System.out.println("Order " + masterOrder.getId() + " has been cancelled due to timeout. Stock restored.");
        }
    }
}
