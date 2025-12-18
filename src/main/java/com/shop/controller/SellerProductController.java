package com.shop.controller;

import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import com.shop.entity.Result;
import com.shop.service.SellerProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/seller")
public class SellerProductController {
    @Autowired
    private SellerProductService sellPro;

    @PreAuthorize("hasRole('SELLER')")
    @RequestMapping(method = RequestMethod.POST,value = "/Pro")
    public Result insertProduct(@RequestBody Product product){
        int insertResult = sellPro.insertProduct(product);
        if(insertResult >0){
            return Result.success("success");
        }
        return Result.error("登入賣場 失敗");

    }

    @PreAuthorize("hasRole('SELLER')")
    @RequestMapping(method = RequestMethod.GET ,value = "/Pro/{id}")
    public Result findProdcutDetail(@PathVariable int id){
        Product product = sellPro.findProdcutById(id);
        if(product != null){
            return Result.success(product);
        }
        return Result.error("失敗 請再次嘗試");
    }

    @PreAuthorize("hasRole('SELLER')")
    @RequestMapping(method = RequestMethod.PUT ,value = "/Pro/{id}")
    //更新商品資料
    public Result updateProductById(@PathVariable int id , @RequestBody Product newProduct){
        int  updateResult = sellPro.updateProductById(id , newProduct);
        if(updateResult > 0){
            return Result.success();
        }
        return Result.error("失敗 請再次嘗試");

    }

    @PreAuthorize("hasRole('SELLER')")
    @RequestMapping(method = RequestMethod.DELETE ,value = "/Pro/{id}")
    //刪除商品
    public Result deleteProductById(@PathVariable int id){
        int deleteResult = sellPro.deleteProductById(id);
        if(deleteResult>0){
            return Result.success();
        }
        return Result.error("失敗 請再次嘗試");

    }

    @PreAuthorize("hasRole('SELLER')")
    @RequestMapping(method = RequestMethod.GET , value = "/Pro")
    public Result<ProductPage<Product>> findProductPageBySeller(Integer pageNum , Integer pageSize){
        ProductPage<Product> productPage = sellPro.findProductPage(pageNum,pageSize);
        if(productPage.getProductList() != null){
            return Result.success(productPage);
        }
        return Result.error("失敗 請再次嘗試");
    }


}
