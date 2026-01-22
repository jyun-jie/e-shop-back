package com.shop.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shop.Exception.AccessDeniedException;
import com.shop.dto.CreateLogisticsForOrderDto;
import com.shop.dto.CreateLogisticsOrderDto;
import com.shop.dto.InOrderProductDto;
import com.shop.dto.SalesOrderDto;
import com.shop.dto.SentShipOrderDto;
import com.shop.entity.*;
import com.shop.mapper.*;
import com.shop.service.LogisticsService;
import com.shop.service.SalesOrderService;
import com.shop.service.SellerService;
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

    @Autowired
    private SellerService sellerService;
    
    @Autowired
    private LogisticsService logisticsService;
    
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PaymentMapper paymentMapper;


    @Transactional(readOnly = true)
    @Override
    public List<SalesOrderDto> getSalesOrderByState(String mode , String orderState , String storeType) {
        Seller seller = sellerService.getActiveSellerOrThrow();
        int userId = userService.findIdbyName();
        log.info("賣家 {} 正在查詢狀態為 {} 的銷售訂單", seller.getId(), orderState);

        List<Order>  salesOrderList ;
        if("UNCHECKED".equals(mode)){
            salesOrderList =  salesOrderMapper.findNonCreatedLogisticsOrders(
                    seller.getId()
            );
        }else{
            salesOrderList =  salesOrderMapper.findOrderbyState(seller.getId(),orderState);
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
        sellerService.getActiveSellerOrThrow();

        List<Integer> orderIds = shippedOrderIds.getShipOrderList();
        for(int index = 0 ; index<orderIds.size() ; index++){
            int id =orderIds.get(index);
            salesOrderMapper.setStateToShipping(id);
        }
        return true ;
    }

    @Transactional( rollbackFor = Exception.class)
    @Override
    public List<SalesOrderDto> getNonCreatedLogisticsOrders(String storeType) {

        Seller seller = sellerService.getActiveSellerOrThrow();
        log.info("賣家 {} 正在查詢未建立物流單的訂單", seller.getId());

        List<SalesOrderDto> order= getSalesOrderByState( "UNCHECKED", "UNCHECKED" , storeType) ;
        //List<Order> order = salesOrderMapper.findNonCreatedLogisticsOrders(seller.getId() , OrderState.Not_Ship);

        if (order == null ) {
            log.debug("賣家 {} 無任何未建立物流單的訂單", seller.getId());
            return null; // 💡 建議回傳空 List 而不是 null，避免前端 NPE
        }
        return order ;
    };



    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createLogisticsForOrder(CreateLogisticsForOrderDto request) throws JsonProcessingException {
        Seller seller = sellerService.getActiveSellerOrThrow();
        int userId = userService.findIdbyName() ;

        List<Integer> sucess = new ArrayList<>() ;
        for(  Integer orderId : request.getCheckOrderList() ){
            Order order = buyerOrderMapper.selectOrderById(orderId);
            System.out.println("order :" + order);
            if (orderId == null) {
                log.info("订单不存在: orderId={}", orderId);
                throw new RuntimeException("订单不存在: " + orderId);

            }

            // 验证订单是否属于当前卖家
            if (order.getSellerId() != seller.getId()) {
                log.info("无权操作此订单: orderId={}", orderId);
                throw new RuntimeException("无权操作此订单");
            }

            // 检查订单状态
            if (order.getState() != OrderState.UNCHECKED) {
                log.info("订单状态不正确，无法建立物流单: orderId={}, state={}", orderId, order.getState());
                throw new RuntimeException("订单状态不正确，无法建立物流单: " + order.getState());
            }

            // 检查是否为超商取货订单
            if (!"C2C".equals(order.getDeliveryType())) {
                log.info("此订单不是超商取货订单，无法使用物流服务: orderId={}", orderId);
                throw new RuntimeException("此订单不是超商取货订单，无法使用物流服务");
            }

            // 获取买家信息（收件人）

//            User buyer = findUserById(order.getUserId());
//            if (buyer == null) {
//                System.out.println("user2" );
//                log.info("找不到买家信息: userId={}", order.getUserId());
//                throw new RuntimeException("找不到买家信息");
//            }

            // 获取卖家信息（寄件人）
            User sellerUser = findUserById(userId);

            // 构建创建物流单请求
            CreateLogisticsOrderDto logisticsRequest = new CreateLogisticsOrderDto();
            logisticsRequest.setOrderId(orderId);
            logisticsRequest.setMasterOrderId(order.getMasterOrderId());
            logisticsRequest.setDeliveryType("C2C");

            logisticsRequest.setStoreType(order.getPickupStoreType());
            logisticsRequest.setStoreId(order.getPickupStoreId());
            logisticsRequest.setStoreName(order.getPickupStoreName());

            // 寄件人信息（卖家）
            logisticsRequest.setSellerId(seller.getId());
            logisticsRequest.setSenderName(sellerUser.getUsername());
            logisticsRequest.setSenderPhone(sellerUser.getPhone());

            // 收件人信息（买家）
            logisticsRequest.setBuyerId(order.getUserId());
            logisticsRequest.setReceiverName(order.getReceiverName());
            logisticsRequest.setReceiverPhone(order.getReceiverPhone());
            logisticsRequest.setReceiverEmail(order.getReceiverEmail());


            logisticsRequest.setGoodsAmount((int) order.getTotal());

            // 判断是否为取货付款（COD）
            // 如果订单的支付方式是COD，则为取货付款
            MasterOrder masterOrder = masterOrderMapper.findById(order.getMasterOrderId());

            boolean isCod = masterOrder != null &&
                    masterOrder.getPay_method() == pay_method.COD;

            logisticsRequest.setIsCod(isCod);
            if (isCod) {
                logisticsRequest.setCodAmount((int) order.getTotal());
            }


            String MerchantOrderNo = paymentMapper.findTradeNoByMasterOrderId(masterOrder.getId());
            logisticsRequest.setMerchantOrderNo(MerchantOrderNo);

            // 调用物流服务创建物流单
            int logisticsId = logisticsService.createLogisticsOrder(logisticsRequest);
            sucess.add(orderId);

            buyerOrderMapper.updateOrderState(orderId , OrderState.Not_Ship);
            log.info("为订单创建物流单成功: orderId={}, logisticsId={}", orderId, logisticsId);
        }


        //return "" ;
        return String.valueOf(sucess);
    }

    
    @Autowired
    private com.shop.mapper.MasterOrderMapper masterOrderMapper;
    
    // 使用BaseMapper的方法查找用户
    private User findUserById(int userId) {
        return userMapper.selectById(userId);
    }


}
