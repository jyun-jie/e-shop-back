package com.shop.service.serviceImpl;


import com.shop.dto.BuyerOrderDto;
import com.shop.dto.CreateOrderRequestDTO;
import com.shop.dto.InOrderProductDto;
import com.shop.dto.OrderDto;
import com.shop.entity.*;
import com.shop.mapper.BuyerOrderMapper;
import com.shop.mapper.MasterOrderMapper;
import com.shop.service.BuyerOrderService;
import com.shop.service.SellerProductService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

@Service
public class BuyerOrderServiceImpl implements BuyerOrderService {
    @Autowired
    private UserService userService;
    @Autowired
    private SellerProductService sellerProductService;
    @Autowired
    private BuyerOrderMapper buyerOrderMapper;
    @Autowired
    private MasterOrderMapper masterOrderMapper;





    @Override
    @Transactional(readOnly = true)
    public List<Cart> generateCheckedOrder(List<CartProduct> productList) {
        return mergeSameSellerId(productList);
    }


    public List<Cart> mergeSameSellerId(List<CartProduct> cartProductList) {
        List<Cart> cartList = new ArrayList<>();
        for (CartProduct cartProduct : cartProductList) {
            Product productDetail = sellerProductService.findProdcutById(cartProduct.getId());

            //如果Cart(依名稱劃分) 不存在 就創建並加入產品
            if (cartList.isEmpty()) {
                cartList = createAndAddCart(cartList, productDetail, cartProduct);
            } else {
                cartList = addProductIfCartExist(cartList, productDetail, cartProduct);
            }
        }
        return cartList;
    }

    public List<Cart> createAndAddCart(List<Cart> cartList, Product productDetail, CartProduct cartProduct) {
        Cart newCart = new Cart();
        List<CartProduct> cartProductList = new ArrayList<>();

        newCart.setSellerId(productDetail.getSellerId());
        cartProductList.add(cartProduct);
        newCart.setCartProductList(cartProductList);
        newCart.setTotal(cartProduct.getPrice()*cartProduct.getQuantity());
        cartList.add(newCart);
        return cartList;
    }

    public List<Cart> addProductIfCartExist(List<Cart> cartList, Product productDetail, CartProduct cartProduct) {
        for (Cart cart : cartList) {
            if (cart.getSellerId() == productDetail.getSellerId()) {
                cart.getCartProductList().add(cartProduct);
                cart.setTotal(cart.getTotal()+(cartProduct.getPrice()* cartProduct.getQuantity()));
                return cartList;
            }
        }
        return createAndAddCart(cartList, productDetail, cartProduct);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertOrderList(CreateOrderRequestDTO createOrderRequest){
        int totalAmount = 0 ;
        int buyerId = userService.findIdbyName();
        List<Cart> cartList = createOrderRequest.getCartList();
        String payment_method = createOrderRequest.getPayment_method();
        String receiverAddress = createOrderRequest.getReceiverAddress();

        for (Cart cart : cartList) {
            for (CartProduct cartProduct : cart.getCartProductList()) {
                int currentStock = buyerOrderMapper.shrinkStock(cartProduct.getId() , cartProduct.getQuantity());
                if (currentStock ==0  ) {
                    throw new RuntimeException("產品 " + cartProduct.getName() + " 庫存不足或已下架");
                }

                totalAmount += cartProduct.getPrice() * cartProduct.getQuantity();
            }
        }

        MasterOrder masterOrder = new MasterOrder();
        masterOrder.setBuyer_id(buyerId);
        masterOrder.setTotal_amount(totalAmount);
        masterOrder.setPay_method(pay_method.valueOf(payment_method));

        masterOrderMapper.insertMasterOrder(masterOrder);
        int masterOrderId = masterOrder.getId();



        //List<Integer> orderIdList= new ArrayList<Integer>();
        for(Cart cart :cartList){
            int orderId = insertOrder(cart , masterOrderId ,buyerId , receiverAddress);
            insertInOrderProduct(cart,orderId);
        }
        return masterOrderId ;
    }

    public int insertOrder(Cart cart, int masterOrderId ,int buyerId , String receiverAddress) {
        String username = userService.findNamebyId(buyerId);
        String sellerName = userService.findNamebyId(cart.getSellerId());
        Order order = new Order();
        order.setMaster_order_id(masterOrderId);
        order.setUserId(buyerId);
        order.setSellerId(cart.getSellerId());
        order.setState(OrderState.Not_Ship);
        order.setTotal(cart.getTotal());
        order.setReceiverAddress(receiverAddress);
        order.setPostalName(sellerName);
        order.setReceiverName(username);
        System.out.println(order);
        buyerOrderMapper.insertOrder(order);

        return (order.getId());
    }

    public void insertInOrderProduct(Cart cart,int orderId){
        for(CartProduct product : cart.getCartProductList()){
            buyerOrderMapper.insertInOrderProduct(product,orderId);
        }
    }


    @Override
    public List<OrderDto> getUserOrderByState(String type){
        int userId = userService.findIdbyName();
        return getPurchaseOrderList(userId,type);

    }

    public List<OrderDto> getPurchaseOrderList(int userId, String type){
        List<Order> orderList ;

        if(type.equals("Not_Paid")){
            orderList =  buyerOrderMapper.selectNotPaidListByUserId(userId);
        }else{
            orderList =  buyerOrderMapper.selectListByUserIdAndState(userId,type);
        }
        return getOrderList(orderList);
    }

    public List<OrderDto> getOrderList(List<Order> orderList){
        List<OrderDto> purchaseList = new ArrayList<>();
        for(Order order : orderList){
            OrderDto purchaseOrder = new OrderDto();

            purchaseOrder.setId(order.getId());
            purchaseOrder.setSellerName(order.getPostalName());
            purchaseOrder.setTotal(order.getTotal());

            List<InOrderProductDto> purchaseProductList =getOrderProductList(order.getId());
            purchaseOrder.setOrderProductList(purchaseProductList);
            purchaseList.add(purchaseOrder);
        }
        return purchaseList;
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
    public void changeStateToCompleted(BuyerOrderDto pickupOrderList){
        List<Integer> OrderList = pickupOrderList.getPickupOrderList();
        for(int index=0;index<OrderList.size();index++){
            int orderId = OrderList.get(index);
            buyerOrderMapper.changeStateToCompleted(orderId);
        }
    }


}
