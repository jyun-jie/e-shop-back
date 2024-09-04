package com.shop.controller;

import com.google.gson.Gson;
import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import com.shop.entity.Result;
import com.shop.mapper.SellerProductMapper;
import com.shop.service.JwtService;
import com.shop.service.serviceimpl.SellerProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;


@WebMvcTest( controllers = SellerProductController.class) // 適合 WEB LAYER 專注於HTTP / request or response
@AutoConfigureMockMvc(addFilters = false)
public class SellerProductControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    SellerProductServiceImpl sellPro;
    @MockBean
    SellerProductMapper sellerProductMapper;

    @MockBean
    JwtService jwtService;
    @InjectMocks
    SellerProductController sellerProductController;

    Product expectProduct;
    Product product;
    String json;


    @BeforeEach
    void setup(){
        product = new Product(2,"玩具","玩具類","這是個玩具","xxx","123",12.2,2,12,5.0);
        expectProduct =
                new Product(2,"玩具","玩具類","" +
                        "這是個玩具","xxx","" +
                        "123",12.2,2,12,5.0);

        json = """
                {   
                    id:1,name:玩具,category:玩具類,description:這是個玩具," +
                "imageUrl:xxx,manufacturer:123,price:12.2,stock:2,sales:1234,rating:5.0 
                }
                """;
    }


    @Test
    void SellerProductController_insertProduct_returnResult() throws Exception {
        //given
        Gson gson = new Gson();




        Mockito.when(sellPro.insertProduct(product)).thenReturn(1);


        ResultActions  resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/seller/Pro")
                        .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(product)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(gson.toJson(Result.success())));

//        Result actualResponse = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),Result.class);
//        String j = gson.toJson(Result.success());
//        Assertions.assertEquals(gson.fromJson(j,Result.class),actualResponse );
    }

    @Test
    void SellerProductController_findProdcutDetail_returnResult() throws Exception {
        //given
        Gson gson = new Gson();
        Mockito.when(sellPro.findProdcutById(2)).thenReturn(expectProduct);


        ResultActions  resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/seller/Pro/2")
                        .contentType(MediaType.APPLICATION_JSON_UTF8).characterEncoding("UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andReturn().getResponse().setDefaultCharacterEncoding("UTF-8");


        String stringResult = gson.toJson(Result.success(expectProduct));
        String actualProduct = resultActions.andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(stringResult, actualProduct);
    }

    @Test
    void SellerProductController_updateProductById_returnResult() throws Exception {
        //given
        Gson gson = new Gson();
        Mockito.when(sellPro.updateProductById(2,product)).thenReturn(1);


        ResultActions  resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/seller/Pro/2")
                        .contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(product)).characterEncoding("UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andReturn().getResponse().setDefaultCharacterEncoding("UTF-8");


        Result actualResponse = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),Result.class);
        Assertions.assertEquals(Result.success(), actualResponse);
    }

    @Test
    void SellerProductController_deleteProductById_returnResult() throws Exception {
        //given
        Gson gson = new Gson();
        Mockito.when(sellPro.deleteProductById(2)).thenReturn(1);


        ResultActions  resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/seller/Pro/2")
                        .contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(product)).characterEncoding("UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andReturn().getResponse().setDefaultCharacterEncoding("UTF-8");


        Result actualResponse = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),Result.class);
        Assertions.assertEquals(Result.success(), actualResponse);
    }


    @Test
    void SellerProductController_findProductPageBySeller_returnResult() throws Exception {
        //given
        Gson gson = new Gson();
        int pageNum = 0;
        int pageSize = 2;
        Product product1 = new Product(2, "玩具", "玩具類", "這是個玩具", "xxx", "123", 12.2, 2, 12, 5.0);
        Product product2 = new Product(4, "牙膏", "生活類", "這是個牙膏", "xxx", "123", 12.2, 2, 12, 4.0);

        List<Product> productList = new ArrayList<>();
        productList.add(product1);
        productList.add(product2);
        Mockito.when(sellPro.findProductPage(pageNum, pageSize))
                .thenReturn(new ProductPage<>(pageNum+pageSize,productList));


        //when
        ResultActions  resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/seller/Pro")
                        .param("pageNum","0").param("pageSize","2")
                        .contentType(MediaType.APPLICATION_JSON_UTF8).characterEncoding("UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andReturn().getResponse().setDefaultCharacterEncoding("UTF-8");


        //then
        String stringResult = gson.toJson(Result.success(new ProductPage<>(pageNum+pageSize,productList)));
        String actualProduct = resultActions.andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(stringResult, actualProduct);
    }




}
