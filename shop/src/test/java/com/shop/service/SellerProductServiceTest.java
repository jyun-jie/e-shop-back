package com.shop.service;

import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import com.shop.mapper.SellerProductMapper;
import com.shop.service.serviceimpl.SellerProductServiceImpl;
import com.shop.service.serviceimpl.UserServiceImpl;
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

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;


@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class) //適合BUSINESS LOGIC
public class SellerProductServiceTest {

    @Mock
    SellerProductMapper sellerProductMapper;
    @Mock
    UserServiceImpl userService;

    @InjectMocks
    SellerProductServiceImpl sellerProductService;

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
    void setSellerProductService_insertProduct_returnOneOrZero(){
        when(userService.findIdbyName()).thenReturn(12);
        when(sellerProductMapper.insertProduct(eq(12),Mockito.any(Product.class))).thenReturn(1);

        int i  = sellerProductService.insertProduct(expectProduct);


        Assertions.assertEquals(1,i);
    }

    @Test
    void setSellerProductService_findProductById_returnProduct(){
        when(sellerProductMapper.selectProductById(eq(2))).thenReturn(product);


        Product product = sellerProductService.findProdcutById(2);

        Assertions.assertEquals(expectProduct,product);
        Assertions.assertEquals(2,product.getId());
    }


    @Test
    void setSellerProductService_updateProductById_returnInt(){

        when(sellerProductMapper.updateProduct(eq(2),Mockito.any(Product.class))).thenReturn(1);

        int isOne = sellerProductService.updateProductById(productId,product);

        Assertions.assertEquals(1,isOne);
    }

    @Test
    void setSellerProductService_deleteProductById_returnInt(){

        when(sellerProductMapper.deleteProduct(eq(2))).thenReturn(1);

        int isOne = sellerProductService.deleteProductById(productId);

        Assertions.assertEquals(1,isOne);
    }

    @Test
    void setSellerProductService_findProductPage_returnProductPage(){
        int sellerId = 12;
        int pageNum = 0;
        int pageSize = 2;
        Product product1 = new Product(2, "玩具", "玩具類", "這是個玩具", "xxx", "123", 12.2, 2, 12, 5.0);
        Product product2 = new Product(4, "牙膏", "生活類", "這是個牙膏", "xxx", "123", 12.2, 2, 12, 4.0);

        List<Product> productList = new ArrayList<>();
        productList.add(product1);
        productList.add(product2);

        //when
        when(userService.findIdbyName()).thenReturn(12).thenReturn(sellerId);
        when(sellerProductMapper.selectProductPageBySellerId(eq(0),eq(2),eq(12)))
                .thenReturn(productList);

        ProductPage productPage= sellerProductService.findProductPage(pageNum,pageSize);

        //then
        Assertions.assertEquals(2,productPage.getProductList().size());
        Assertions.assertEquals(product1,productPage.getProductList().get(0));
        Assertions.assertEquals(product2,productPage.getProductList().get(1));
        Assertions.assertEquals(pageNum+pageSize,productPage.getPageNum());

    }

}
