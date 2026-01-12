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

    /**
     * 每分鐘檢查一次超時未付款訂單
     * 超時時間：30分鐘
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void checkTimeoutOrders() {
        // 設定超時時間為 30 分鐘前
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(30);

        // 1. 查詢所有超時且未付款 (INIT) 的 MasterOrder
        List<MasterOrder> expiredOrders = masterOrderMapper.selectExpiredUnpaidOrders(timeoutThreshold);

        if (expiredOrders.isEmpty()) {
            return;
        }

        for (MasterOrder masterOrder : expiredOrders) {
            // 2. 更新 MasterOrder 狀態為 CANCELLED
            masterOrderMapper.updateStatus(masterOrder.getId(), "FAILED");

            // 3. 更新所有子訂單狀態為 CANCELLED
            buyerOrderMapper.updateStateByMasterOrderId(masterOrder.getId(), OrderState.CANCELLED);

            // 4. 回補庫存
            List<Order> subOrders = buyerOrderMapper.selectByMasterOrderId(masterOrder.getId());
            for (Order order : subOrders) {
                // 查詢該訂單下的所有商品
                List<InOrderProduct> products = buyerOrderMapper.selectInOrderproductByOrderId(order.getId());
                for (InOrderProduct product : products) {
                    // 將商品數量加回庫存
                    buyerOrderMapper.increaseStock(product.getProduct_Id(), product.getQuantity());
                }
            }
            
            System.out.println("Order " + masterOrder.getId() + " has been cancelled due to timeout. Stock restored.");
        }
    }
}
