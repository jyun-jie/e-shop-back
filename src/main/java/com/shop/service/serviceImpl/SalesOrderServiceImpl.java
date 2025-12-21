package com.shop.service.serviceImpl;

import com.shop.dto.InOrderProductDto;
import com.shop.dto.SalesOrderDto;
import com.shop.dto.SentShipOrderDto;
import com.shop.entity.InOrderProduct;
import com.shop.entity.Order;
import com.shop.mapper.BuyerOrderMapper;
import com.shop.mapper.SalesOrderMapper;
import com.shop.service.SalesOrderService;
import com.shop.service.UserService;
import jakarta.persistence.RollbackException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    @Autowired
    private BuyerOrderMapper buyerOrderMapper;


    @Transactional(readOnly = true)
    @Override
    public List<SalesOrderDto> getSalesOrderByState(String orderState) {
        int userId = userService.findIdbyName();
        log.info("使用者 {} 正在查詢狀態為 {} 的銷售訂單", userId, orderState);

        List<Order>  salesOrderList ;
        if(orderState.equals("Not_Paid")){
             salesOrderList = salesOrderMapper.findNotPaidOrderbyId(userId);
        }else{
             salesOrderList = salesOrderMapper.findOrderbyId(userId,orderState);
        }

        if (salesOrderList == null || salesOrderList.isEmpty()) {
            log.debug("使用者 {} 無任何 {} 狀態的訂單", userId, orderState);
            return Collections.emptyList(); // 💡 建議回傳空 List 而不是 null，避免前端 NPE
        }
        return getSalesOrderList(salesOrderList);
    }

    public List<SalesOrderDto> getSalesOrderList(List<Order> orderList ){
        List<SalesOrderDto> salesOrderList = new ArrayList<>();
        for(Order order : orderList){
            SalesOrderDto salesOrder = new SalesOrderDto();

            salesOrder.setId(order.getId());
            salesOrder.setSellerName(order.getPostalName());
            salesOrder.setTotal(order.getTotal());

            // 💡 效能警告：這裡是 N+1 問題的發生點
            List<InOrderProductDto> purchaseProductList =getOrderProductList(order.getId());
            salesOrder.setOrderProductList(purchaseProductList);
            salesOrderList.add(salesOrder);
        }
        return salesOrderList;
    }

    public List<InOrderProductDto> getOrderProductList(int orderId){
        List<InOrderProductDto> purchaseProductList = new ArrayList<>();
        List<InOrderProduct> inOrderProductsList = buyerOrderMapper.selectInOrderproductByOrderId(orderId);

        for(InOrderProduct inOrderProduct : inOrderProductsList){
            InOrderProductDto inOrderProductDto = new InOrderProductDto();

            inOrderProductDto.setProduct_Id(inOrderProduct.getProduct_Id());
            inOrderProductDto.setProductName(inOrderProduct.getProductName());
            inOrderProductDto.setPrice(inOrderProduct.getPrice());
            inOrderProductDto.setQuantity(inOrderProduct.getQuantity());
            purchaseProductList.add(inOrderProductDto);
        }
        return purchaseProductList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean sentShippedOrders(SentShipOrderDto shippedOrderIds){
        List<Integer> orderIds = shippedOrderIds.getShipOrderList();
        for(int index = 0 ; index<orderIds.size() ; index++){
            int id =orderIds.get(index);
            salesOrderMapper.setStateToShipping(id);
        }
        return true ;
    }


}
