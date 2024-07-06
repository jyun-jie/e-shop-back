package com.shop.controller;

import com.shop.dto.ProductDto;
import com.shop.entity.ProductPage;
import com.shop.entity.Product;
import com.shop.entity.Result;
import com.shop.service.SellerProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/seller")
public class SellerProductController {

    @Autowired
    private SellerProductService sellPro;

    //新增商品
    @RequestMapping(method = RequestMethod.POST,value = "/Pro")
    public Result insertProduct(@RequestBody Product product){
        int insertProduct = sellPro.insertProduct(product);
        if(insertProduct > 0){
            return Result.success("成功登入賣場");
        }else{
            return Result.error("登入賣場 失敗");
        }
    }

    @RequestMapping(method = RequestMethod.GET ,value = "/Pro/{id}")
    //獲取某單一商品訊息
    public Result findProdcutById(@PathVariable int id){
        ProductDto product = sellPro.findProdcutById(id);
        if(product != null){
            return Result.success(product);
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }

    @RequestMapping(method = RequestMethod.PUT ,value = "/Pro/{id}")
    //更新商品資料
    public Result updateProductById(@PathVariable int id , @RequestBody Product newProduct){
        int  updateProduct = sellPro.updateProductById(id , newProduct);
        if(updateProduct > 0){
            return Result.success("成功");
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }

    @RequestMapping(method = RequestMethod.DELETE ,value = "/Pro/{id}")
    //刪除商品
    public Result deleteProductById(@PathVariable int id){
        int deleteProduct = sellPro.deleteProductById(id);
        if(deleteProduct>0){
            return Result.success("成功 刪除");
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }

    @RequestMapping(method = RequestMethod.GET , value = "/Pro")
    public Result<ProductPage<Product>> findProductPageBySeller(Integer pageNum , Integer pageSize){
        ProductPage<Product> productPage = sellPro.findProductPage(pageNum,pageSize);
        if(productPage != null){
            return Result.success(productPage);
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }


}
