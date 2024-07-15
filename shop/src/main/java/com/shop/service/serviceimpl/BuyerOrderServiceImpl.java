package com.shop.service.serviceimpl;


import com.shop.dto.InOrderProductDto;
import com.shop.dto.OrderDto;
import com.shop.entity.*;
import com.shop.mapper.BuyerOrderMapper;
import com.shop.service.BuyerOrderService;
import com.shop.service.SellerProductService;
import com.shop.service.UserService;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<Cart> generateCheckedOrder(List<CartProduct> productList) {
        List<Cart> cartList = mergeSameSellerId(productList);
        return cartList;
    }

    public List<Cart> mergeSameSellerId(List<CartProduct> cartProductList) {
        List<Cart> cartList = new ArrayList<>();
        for (CartProduct cartProduct : cartProductList) {
            Product productDetail = sellerProductService.findProdcutById(cartProduct.getId());
            //如果List是空的
            if (cartList.isEmpty()) {
                cartList = getAndSetCart(cartList, productDetail, cartProduct);
            } else {
                cartList =getCartIfCartSellerIdExist(cartList, productDetail, cartProduct);
            }
        }
        return cartList;
    }

    public List<Cart> getAndSetCart(List<Cart> cartList, Product productDetail, CartProduct cartProduct) {
        Cart newCart = new Cart();
        List<CartProduct> cartProductList = new ArrayList<>();

        newCart.setSellerId(productDetail.getSellerId());
        cartProductList.add(cartProduct);
        newCart.setCartProductList(cartProductList);
        newCart.setTotal(cartProduct.getPrice()*cartProduct.getQuantity());
        cartList.add(newCart);
        return cartList;
    }

    public List<Cart> getCartIfCartSellerIdExist(List<Cart> cartList, Product productDetail, CartProduct cartProduct) {
        for (Cart cart : cartList) {
            if (cart.getSellerId() == productDetail.getSellerId()) {
                cart.getCartProductList().add(cartProduct);
                cart.setTotal(cart.getTotal()+(cartProduct.getPrice()* cartProduct.getQuantity()));
                return cartList;
            }
        }
        return getAndSetCart(cartList, productDetail, cartProduct);
    }

    public Boolean insertOrderList(List<Cart> cartList){
        for(Cart cart :cartList){
            int orderId = insertOrder(cart,OrderState.To_Ship);
            insertInOrderProduct(cart,orderId);
        }
        return true;
    }

    public int insertOrder(Cart cart, OrderState orderState){
        int userId = userService.findIdbyName();
        String username = userService.findNamebyId(userId);
        String sellerName = userService.findNamebyId(cart.getSellerId());
        Order order = new Order();
        order.setUserId(userId);
        order.setSellerId(cart.getSellerId());
        order.setState(orderState);
        order.setTotal(cart.getTotal());
        order.setReceiverAddress(cart.getReceiverAddress());
        order.setPostalName(sellerName);
        order.setReceiverName(username);
        order.setPayment_method("0");
        System.out.println(order);
        buyerOrderMapper.insertOrder(order);
        return (order.getId());
    }

    public void insertInOrderProduct(Cart cart,int orderId){
        for(CartProduct product : cart.getCartProductList()){
            buyerOrderMapper.insertInOrderProduct(product,orderId);
        }
    }

    public List<OrderDto> getUserOrderByState(String type){
        int userId = userService.findIdbyName();
        List<OrderDto> purchaseList = getPurchaseOrderList(userId,type);
        return purchaseList;
    }

    public List<OrderDto> getPurchaseOrderList(int userId, String type){
        List<Order> orderList ;

        if(type.equals("Not_Paid")){
            orderList =  buyerOrderMapper.selectNotPaidListByUserId(userId);
        }else{
            orderList =  buyerOrderMapper.selectListByUserIdAndState(userId,type);
        }
        List<OrderDto> purchaseList = getOrderList(orderList);

        return purchaseList;
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

    public void changeStateToReceivedOrCompleted(int orderId){
        buyerOrderMapper.changeStateToReceived(orderId);
        checkIfChangeStatusToCompleted(orderId);
    }

    public void checkIfChangeStatusToCompleted(int orderId){
        Order order = buyerOrderMapper.selectByOrderId(orderId);
        if(order.getIsPay() ==  true){
            buyerOrderMapper.changeStateToCompleted(orderId);
        }
    }

}
