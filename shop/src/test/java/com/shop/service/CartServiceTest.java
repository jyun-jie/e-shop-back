package com.shop.service;

import com.shop.entity.Cart;
import com.shop.entity.CartProduct;
import com.shop.entity.Product;
import com.shop.mapper.CartMapper;
import com.shop.service.serviceimpl.CartServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    RedisTemplate<String, String> redisTemplate;
    @Mock
    HashOperations hashOperations;
    @Mock
    CartMapper cartMapper;
    @Mock
    UserService userService;
    @InjectMocks
    CartServiceImpl CartService;

    Product expectProduct, product;

    String json,cartListJson;

    List<Cart>  cartList ;
    List<CartProduct> cartProductList1,cartProductList2;

    CartProduct cartProduct1 =new CartProduct(14,"com1",1234,2);
    CartProduct cartProduct2 =new CartProduct(20,"com24",4567,10);
    CartProduct cartProduct3 =new CartProduct(14,"com1",1234,5);
    CartProduct cartProduct4 =new CartProduct(16,"com13",41,6);

    int productId;

    @BeforeEach
    void setup(){
        product = new Product(14,"com1","玩具類","" +
                "這是個玩具","xxx","" +
                "123",1234,2,12,5.0);

        expectProduct =
                new Product(14,"com1","玩具類","" +
                        "這是個玩具","xxx","" +
                        "123",1234,2,12,5.0);


        json = "{\"id\":1,\"name\":\"玩具\",\"category\":\"玩具類\",\"description\":\"這是個玩具\"," +
                "\"imageUrl\":\"xxx\",\"manufacturer\":\"123\",\"price\":12.2,\"stock\":2,\"sales\":1234,\"rating\":5.0}";

        cartListJson = "[{\"sellerId\":12,\"cartProductList\":[{\"id\":14,\"name\":\"com1\",\"price\":1234,\"quantity\":2}," +
                "{\"id\":20,\"name\":\"com24\",\"price\":4567,\"quantity\":10}]}]";

        productId = 14;
        cartList = new ArrayList<>();
        cartProductList1 = new ArrayList<>();
        cartProductList2 = new ArrayList<>();





    }

    @Test
    void setCartService_insertProductToCart_returnCartProductQuantityEquals5() {
        int seller1Id = 12;
        int userId = 10;
        cartProductList1.add(cartProduct1);
        cartProductList1.add(cartProduct2);
        cartProductList2.add(cartProduct3);
        cartProductList2.add(cartProduct2);
        Cart cart1 = new Cart();
        cart1.setSellerId(seller1Id);
        cart1.setCartProductList(cartProductList1);

        Cart cart2 = new Cart();
        cart2.setSellerId(seller1Id);
        cart2.setCartProductList(cartProductList2);
        cartList.add(cart2);




        Mockito.when(cartMapper.selectProductById(productId)).thenReturn(expectProduct);
        Mockito.when(userService.findIdbyName()).thenReturn(userId);
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        Mockito.when(hashOperations.get("Cart",userId )).thenReturn(cartListJson);

        List cartListResponse = CartService.insertProductToCart(productId,3);

        Assertions.assertEquals(cartList,cartListResponse);

    }

    @Test
    void setCartService_findCartListByUser(){
        int userId = 10;
        int sellerId = 12;
        cartProductList1.add(cartProduct1);
        cartProductList1.add(cartProduct2);
        Cart cart1 = new Cart();
        cart1.setSellerId(sellerId);
        cart1.setCartProductList(cartProductList1);
        cartList.add(cart1);

        Mockito.when(userService.findIdbyName()).thenReturn(userId);
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        Mockito.when(hashOperations.get("Cart",userId)).thenReturn(cartListJson);

        List<Cart> cartListResponse = CartService.findCartListByUser();

        Assertions.assertEquals(cartList,cartListResponse);
    }

//    @Test
//    void setCartService_getSellerIdByProduct_returnSellerId() {
//        int SellerIdResponse = CartService.getSellerIdByProduct(expectProduct);
//
//        Assertions.assertEquals(12,SellerIdResponse);
//    }
//
//    @Test
//    void setCartService_findCartListByUser_returnCartList(){
//        int userId = 10;
//        Cart cart = new Cart();
//        cart.setSellerId(12);
//        cartProductList1.add(cartProduct1);
//        cartProductList1.add(cartProduct2);
//        cart.setCartProductList(cartProductList1);
//        cartList.add(cart);
//
//        Mockito.when(userService.findIdbyName()).thenReturn(userId);
//        Mockito.when(redisTemplate.opsForHash()).thenReturn(hashOperations);
//        Mockito.when(hashOperations.get("Cart",userId )).thenReturn(cartListJson);
//
//
//        List<Cart> cartListResponse= CartService.findCartListByUser();
//
//        Assertions.assertEquals(cartList,cartListResponse);
//
//    }
//    @Test
//    void setCartService_getCartByTheSameSeller_returnCart(){
//        int seller1Id = 12;
//        cartProductList1.add(cartProduct1);
//        cartProductList1.add(cartProduct2);
//        cartProductList2.add(cartProduct3);
//        cartProductList2.add(cartProduct2);
//        Cart cart1 = new Cart();
//        cart1.setSellerId(seller1Id);
//        cart1.setCartProductList(cartProductList1);
//
//
//        Cart cart2 = new Cart();
//        cart2.setSellerId(seller1Id);
//        cart2.setCartProductList(cartProductList2);
//        cartList.add(cart2);
//
//
//        Cart cartResponse = CartService.getCartByTheSameSeller(cartList,seller1Id);
//
//        Assertions.assertEquals(cart2,cartResponse);
//    }
//
//    @Test
//    void setCartService_addProductToCart_returnCart(){
//        int seller1Id = 12;
//        cartProductList1.add(cartProduct1);
//        cartProductList1.add(cartProduct2);
//        Cart cart1 = new Cart();
//        cart1.setSellerId(seller1Id);
//        cart1.setCartProductList(cartProductList1);
//
//        CartProduct cartResponse = CartService.addProductToCart(cart1,productId,3);
//
//        Assertions.assertEquals(cartProduct3,cartResponse);
//    }

}

