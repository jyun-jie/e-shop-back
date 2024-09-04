package com.shop.service;

import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import com.shop.mapper.BuyerShoppingMapper;
import com.shop.service.serviceimpl.BuyerShoppingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.ArrayList;
import java.util.List;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class BuyerShoppingServiceTest {

    @Mock
    BuyerShoppingMapper buyerShoppingMapper;

    @InjectMocks
    BuyerShoppingServiceImpl buyerShoppingService;


    Product expectProduct, product;
    String json;
    int productId;
    @BeforeEach
    void setup(){
        product = new Product(2,"玩具","玩具類","" +
                "這是個玩具","xxx","" +
                "123",12.2,2,12,5.0);
        expectProduct =
                new Product(2,"玩具","玩具類","" +
                        "這是個玩具","xxx","" +
                        "123",12.2,2,12,5.0);
        json = "{\"id\":1,\"name\":\"玩具\",\"category\":\"玩具類\",\"description\":\"這是個玩具\"," +
                "\"imageUrl\":\"xxx\",\"manufacturer\":\"123\",\"price\":12.2,\"stock\":2,\"sales\":1234,\"rating\":5.0}";
        productId = 2;

    }

    @Test
    void setBuyerShoppingService_findProductPage_returnProductList() {
        int pageNum = 0;
        int pageSize = 2;
        Product product1 = new Product(2, "玩具", "玩具類", "這是個玩具", "xxx", "123", 12.2, 2, 12, 5.0);
        Product product2 = new Product(4, "牙膏", "生活類", "這是個牙膏", "xxx", "123", 12.2, 2, 12, 4.0);
        List<Product> productList = new ArrayList<>();
        productList.add(product1);
        productList.add(product2);

        Mockito.when(buyerShoppingMapper.selectProductPage(pageNum,pageSize)).thenReturn(productList);


        ProductPage productPage = buyerShoppingService.findProductPage(pageNum,pageSize);

        Assertions.assertEquals(new ProductPage(pageNum+pageSize,productList),productPage);
    }

    @Test
    void setBuyerShoppingService_findProductById_returnProduct(){
        Mockito.when(buyerShoppingMapper.selectProductById(2)).thenReturn(expectProduct);


        Product productResponse = buyerShoppingService.findProductById(2);

        Assertions.assertEquals(product,productResponse);

    }


}
