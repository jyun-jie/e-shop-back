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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    @Autowired
    private BuyerOrderMapper buyerOrderMapper;


    @Override
    public List<SalesOrderDto> getSalesOrderByState(String orderState) {
        int userId = userService.findIdbyName();
        List<Order>  salesOrderList ;
        System.out.println(userId);
        System.out.println(orderState);
        if(orderState.equals("Not_Paid")){
             salesOrderList = salesOrderMapper.findNotPaidOrderbyId(userId);
        }else{
             salesOrderList = salesOrderMapper.findOrderbyId(userId,orderState);
        }
        System.out.println(salesOrderList);
        if(salesOrderList.isEmpty()){
            return null;
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

    public boolean sentShippedOrders(SentShipOrderDto shippedOrderIds){
        List<Integer> orderIds = shippedOrderIds.getShipOrderList();
        for(int index = 0 ; index<orderIds.size() ; index++){
            int id =orderIds.get(index);
            salesOrderMapper.setStateToShipping(id);
        }
        return true ;
    }


}
