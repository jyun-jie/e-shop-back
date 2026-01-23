package com.shop.service.serviceImpl;

import com.shop.dto.BuyerOrderDto;
import com.shop.dto.CreateOrderRequestDTO;
import com.shop.dto.InOrderProductDto;
import com.shop.dto.OrderDto;
import com.shop.entity.*;
import com.shop.mapper.BuyerOrderMapper;
import com.shop.mapper.MasterOrderMapper;
import com.shop.mapper.SellerProductMapper;
import com.shop.mapper.UserMapper;
import com.shop.service.BuyerOrderService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.Receiver;
import java.util.ArrayList;
import java.util.List;

@Service
public class BuyerOrderServiceImpl implements BuyerOrderService {
    @Autowired
    private UserService userService;
    @Autowired
    private BuyerOrderMapper buyerOrderMapper;
    @Autowired
    private MasterOrderMapper masterOrderMapper;
    @Autowired
    private SellerProductMapper sellerProductMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Cart> generateCheckedOrder(List<CartProduct> productList) {
        return mergeSameSellerId(productList);
    }

    public List<Cart> mergeSameSellerId(List<CartProduct> cartProductList) {
        List<Cart> cartList = new ArrayList<>();
        for (CartProduct cartProduct : cartProductList) {
            Product productDetail = sellerProductMapper.selectProductById(cartProduct.getId());


            if (productDetail == null) {
                continue;
            }

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

        cartProduct.setPrice(productDetail.getPrice());
        cartProductList.add(cartProduct);
        newCart.setSellerId(productDetail.getSellerId());
        newCart.setCartProductList(cartProductList);
        newCart.setTotal(productDetail.getPrice() * cartProduct.getQuantity());
        cartList.add(newCart);
        return cartList;
    }

    public List<Cart> addProductIfCartExist(List<Cart> cartList, Product productDetail, CartProduct cartProduct) {
        for (Cart cart : cartList) {
            if (cart.getSellerId() == productDetail.getSellerId()) {
                cartProduct.setPrice(productDetail.getPrice());
                cart.getCartProductList().add(cartProduct);
                cart.setTotal(cart.getTotal() + (productDetail.getPrice() * cartProduct.getQuantity()));
                return cartList;
            }
        }
        return createAndAddCart(cartList, productDetail, cartProduct);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertOrderList(CreateOrderRequestDTO createOrderRequest) {
        double totalAmount = 0;
        int buyerId = userService.findIdbyName();
        List<Cart> cartList = createOrderRequest.getCartList();
        String payment_method = createOrderRequest.getPayment_method();
        String receiverPhone = createOrderRequest.getReceiverPhone();
        String receiverEmail = createOrderRequest.getReceiverEmail();

        for (Cart cart : cartList) {
            double cartRealTotal = 0;

            for (CartProduct cartProduct : cart.getCartProductList()) {
                Product dbProduct = sellerProductMapper.selectProductById(cartProduct.getId());
                if (dbProduct == null) {
                    throw new RuntimeException("產品 ID: " + cartProduct.getId() + " 不存在");
                }

                int currentStock = buyerOrderMapper.shrinkStock(cartProduct.getId(), cartProduct.getQuantity());
                if (currentStock == 0) {
                    throw new RuntimeException("產品 " + dbProduct.getName() + " 庫存不足或已下架");
                }

                double itemTotal = dbProduct.getPrice() * cartProduct.getQuantity();
                cartRealTotal += itemTotal;

                cartProduct.setPrice(dbProduct.getPrice());
            }

            cart.setTotal(cartRealTotal);
            totalAmount += cartRealTotal;
        }

        MasterOrder masterOrder = new MasterOrder();
        masterOrder.setBuyer_id(buyerId);
        masterOrder.setTotal_amount((int) totalAmount);
        masterOrder.setPay_method(pay_method.valueOf(payment_method));


        masterOrderMapper.insertMasterOrder(masterOrder);
        int masterOrderId = masterOrder.getId();


        for (Cart cart : cartList) {
            System.out.println(receiverPhone);
            int orderId = insertOrder(cart, masterOrderId, buyerId, receiverPhone , receiverEmail, payment_method, createOrderRequest);
            insertInOrderProduct(cart, orderId);
        }
        return masterOrderId;
    }

    public int insertOrder(Cart cart, int masterOrderId, int buyerId,String receiverPhone, String receiverEmail,
                          String paymentMethod, CreateOrderRequestDTO createOrderRequest) {
        String username = userService.findNamebyId(buyerId);
        String sellerName = userService.findNamebyId(cart.getSellerId());

        Order order = new Order();
        order.setMasterOrderId(masterOrderId);
        order.setUserId(buyerId);
        order.setSellerId(cart.getSellerId());

        if ("COD".equals(paymentMethod)) {
            order.setState(OrderState.UNCHECKED);
        } else {
            order.setState(OrderState.PENDING_PAYMENT);
        }

        order.setTotal(cart.getTotal());

        if (createOrderRequest.getDeliveryType() != null) {
            order.setDeliveryType(createOrderRequest.getDeliveryType());

            if ("C2C".equals(createOrderRequest.getDeliveryType())) {  //C2C == STORE_PICKUP
                order.setPickupStoreId(createOrderRequest.getPickupStoreId());
                order.setPickupStoreName(createOrderRequest.getPickupStoreName());
                order.setPickupStoreType(createOrderRequest.getPickupStoreType());
                order.setReceiverAddress(createOrderRequest.getPickupStoreName());
            } else {
                User user = userMapper.selectById(buyerId);
                order.setReceiverAddress(user.getAddress());
            }
        } else {
            // 默认宅配
            order.setDeliveryType("HOME_DELIVERY");

        }
        order.setReceiverPhone(receiverPhone);
        order.setReceiverEmail(receiverEmail);
        order.setPostalName(sellerName);
        order.setReceiverName(username);

        int orderId = buyerOrderMapper.insertOrder(order);
        if(orderId ==0){
            throw new RuntimeException("訂單建立失敗");
        }

        return order.getId();
    }

    public void insertInOrderProduct(Cart cart, int orderId) {
        System.out.println(orderId);
        for (CartProduct product : cart.getCartProductList()) {
            buyerOrderMapper.insertInOrderProduct(product, orderId);
        }
    }

    @Override
    public List<OrderDto> getUserOrderByState(String type) {
        int userId = userService.findIdbyName();
        return getPurchaseOrderList(userId, type);
    }

    public List<OrderDto> getPurchaseOrderList(int userId, String type) {
        List<Order> orderList;

        if (type.equals("Not_Paid")) {
            orderList = buyerOrderMapper.selectNotPaidListByUserId(userId);
        } else {
            orderList = buyerOrderMapper.selectListByUserIdAndState(userId, type);
        }
        return getOrderList(orderList);
    }

    public List<OrderDto> getOrderList(List<Order> orderList) {
        List<OrderDto> purchaseList = new ArrayList<>();
        for (Order order : orderList) {
            OrderDto purchaseOrder = new OrderDto();
            purchaseOrder.setId(order.getId());
            purchaseOrder.setSellerName(order.getPostalName());
            purchaseOrder.setTotal(order.getTotal());

            // 注意：此處仍存在 N+1 查詢問題，若訂單量大建議優化 Mapper 使用 JOIN 查詢
            List<InOrderProductDto> purchaseProductList = getOrderProductList(order.getId());
            purchaseOrder.setOrderProductList(purchaseProductList);
            purchaseList.add(purchaseOrder);
        }
        return purchaseList;
    }

    public List<InOrderProductDto> getOrderProductList(int orderId) {
        List<InOrderProductDto> purchaseProductList = new ArrayList<>();
        List<InOrderProduct> inOrderProductsList = buyerOrderMapper.selectInOrderproductByOrderId(orderId);

        for (InOrderProduct inOrderProduct : inOrderProductsList) {
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
    public void changeStateToCompleted(BuyerOrderDto pickupOrderList) {
        List<Integer> OrderList = pickupOrderList.getPickupOrderList();
        for (int index = 0; index < OrderList.size(); index++) {
            int orderId = OrderList.get(index);
            // 建議加入檢查：確認該訂單是否屬於當前登入的使用者，避免惡意操作他人訂單
            buyerOrderMapper.changeStateToCompleted(orderId);
        }
    }
}
