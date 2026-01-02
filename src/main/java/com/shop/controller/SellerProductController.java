package com.shop.controller;

import com.shop.dto.DelImageDto;
import com.shop.dto.HomeProductDto;
import com.shop.dto.ProductDetailDto;
import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import com.shop.entity.Result;
import com.shop.service.ImageService;
import com.shop.service.SellerProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/seller")
public class SellerProductController {
    @Autowired
    private SellerProductService sellPro;

    @Autowired
    private ImageService imageService;


    @PreAuthorize("hasRole('User')")
    @RequestMapping(method = RequestMethod.POST,value = "/Pro" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result insertProduct(@RequestPart("data") ProductDto data,
                                @RequestPart("images") List<MultipartFile> images ,
                                @RequestPart("cover") MultipartFile coverImage
    ) throws IOException {

        int insertResult = sellPro.insertProduct(data,images,coverImage);
        if(insertResult >0){
            return Result.success("success");
        }
        return Result.error("登入賣場 失敗");

    }

    @PreAuthorize("hasRole('User')")
    @RequestMapping(method = RequestMethod.GET ,value = "/Pro/{id}")
    public Result findProdcutDetail(@PathVariable int id){
        List<ProductDetailDto> product = sellPro.findProdcutDetailById(id);
        System.out.println(product);
        if(product != null){
            return Result.success(product);
        }
        return Result.error("失敗 請再次嘗試");
    }

    @PreAuthorize("hasRole('User')")
    @RequestMapping(method = RequestMethod.PUT ,value = "/Pro", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result updateProductById(@RequestPart("data") ProductDto data,
                                    @RequestPart(value = "newImages" , required = false) List<MultipartFile> newImages,
                                    @RequestPart(value = "deleteImages" ,required = false) List<DelImageDto> deletedImages,
                                    @RequestPart(value = "cover" ,required = false) MultipartFile newCover
    ) throws IOException{
        System.out.println(newImages);
        System.out.println(deletedImages);
        System.out.println(newCover);
        int  updateResult = sellPro.updateProductById(data , newImages ,deletedImages ,newCover );
        if(updateResult > 0){
            return Result.success("成功更新");
        }
        return Result.error("失敗 請再次嘗試");
    }

    @PreAuthorize("hasRole('User')")
    @RequestMapping(method = RequestMethod.PUT ,value = "/Pro/delete/{id}")
    //刪除商品
    public Result deleteProductById(@PathVariable int id){
        int deleteResult = sellPro.deleteProductById(id);
        if(deleteResult>0){
            return Result.success();
        }
        return Result.error("失敗 請再次嘗試");
    }

    @PreAuthorize("hasRole('User')")
    @RequestMapping(method = RequestMethod.GET , value = "/Pro")
    public Result<ProductPage<HomeProductDto>> findProductPageBySeller(Integer pageNum , Integer pageSize ,String status){
        System.out.println("進來看資料了"+pageNum+","+pageSize+","+status);
        ProductPage<HomeProductDto> productPage = sellPro.findProductPage(pageNum,pageSize ,status);
        if(productPage.getProductList() != null){
            return Result.success(productPage);
        }
        return Result.error("失敗 請再次嘗試");
    }


    @PreAuthorize("hasRole('User')")
    @RequestMapping(method = RequestMethod.PUT ,value = "/Pro/takenDown/{id}")
    //下架商品
    public Result takenDownProduct(@PathVariable int id){
        System.out.println(id);
        int result = sellPro.takenDownProduct(id);
        if(result>0){
            return Result.success("成功");
        }
        return Result.error("失敗 請再次嘗試");
    }




}
