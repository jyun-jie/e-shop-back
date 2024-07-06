package com.shop.controller;

import com.shop.dto.ProductDto;
import com.shop.entity.ProPage;
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
    public Result insertPro(@RequestBody Product product){
        int i = sellPro.insertPro(product);
        if(i > 0){
            return Result.success("成功登入賣場");
        }else{
            return Result.error("登入賣場 失敗");
        }
    }

    @RequestMapping(method = RequestMethod.GET ,value = "/Pro/{id}")
    //獲取某單一商品訊息
    public Result selectOnePro(@PathVariable int id){
        ProductDto product = sellPro.findProById(id);
        if(product != null){
            return Result.success(product);
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }
    @RequestMapping(method = RequestMethod.PUT ,value = "/Pro/{id}")
    //更新商品資料
    public Result updatePro(@PathVariable int id , @RequestBody Product oldproduct){
        int  i = sellPro.updatePro(id , oldproduct);
        if(i > 0){
            return Result.success("成功");
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }

    @RequestMapping(method = RequestMethod.DELETE ,value = "/Pro/{id}")
    //刪除商品
    public Result deletePro(@PathVariable int id){
        int i = sellPro.deletePro(id);
        if(i>0){
            return Result.success("成功 刪除");
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }

    @RequestMapping(method = RequestMethod.GET , value = "/Pro")
    public Result<ProPage<Product>> selectPro(Integer pageNum , Integer pageSize){
        ProPage<Product> pro = sellPro.loadPro(pageNum,pageSize);
        if(pro != null){
            return Result.success(pro);
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }


}
