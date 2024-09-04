package com.shop.service;

import com.shop.entity.Cart;
import com.shop.entity.CartProduct;
import com.shop.entity.Product;
import com.shop.mapper.BuyerOrderMapper;
import com.shop.service.serviceimpl.BuyerOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.ArrayList;
import java.util.List;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class BuyerOrderServiceTest {

    @Mock
    UserService userService;
    @Mock
    SellerProductService sellerProductService;

    @Mock
    BuyerOrderMapper buyerOrderMapper;
    @InjectMocks
    BuyerOrderServiceImpl buyerOrderService;

    Product expectProduct, product;

    String json,cartListJson;

    List<Cart> cartList ;
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
    void setBuyerOrderService_generateCheckedOrder_returnCartList(){
//        cartProductList1.add(cartProduct1);
//        cartProductList1.add(cartProduct2);
//
//
//        Mockito.when(sellerProductService.findProdcutById(product.getId())).thenReturn(expectProduct);
//
//        List<Cart> cartListResponse= buyerOrderService.generateCheckedOrder(cartProductList1);
//
//        Assertions.assertEquals(0,cartListResponse);
    }


}
