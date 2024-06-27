package com.shop.controller;

import com.shop.dto.ProductDto;
import com.shop.entity.ProPage;
import com.shop.entity.Result;
import com.shop.service.ReadProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/Read")
public class ReadProductController {

    @Autowired
    private ReadProductService ReadProService;

    //查詢所有商品 (分頁)
    @RequestMapping(method = RequestMethod.GET , value = "/unAuth/Pro")
    public Result selectPro(Integer pageNum, Integer pageSize){
        ProPage pro = ReadProService.loadPro(pageNum,pageSize);
        if(pro != null){
            return Result.success(pro);
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }

    //進到產品詳情
    @RequestMapping(method = RequestMethod.GET ,value = "/unAuth/Pro/{id}")
    //獲取某單一商品訊息
    public Result selectProDetl(@PathVariable int id){
        ProductDto product = ReadProService.findProById(id);
        if(product != null){
            return Result.success(product);
        }else{
            return Result.error("失敗 請再次嘗試");
        }
    }

    //
}
