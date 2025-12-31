package com.shop.controller;


import com.shop.entity.Result;
import com.shop.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("public")
public class ImageController {
    @Autowired
    private ImageService imageService;


    @PostMapping("/test/upload")
    public Result upload(@RequestParam("name") String name,
                         @RequestParam("type") String type,
                         @RequestParam("price") Integer price,
                         @RequestParam("quantity") Integer quantity,
                         @RequestParam("address") String address,
                         @RequestParam("description") String description,
                         @RequestParam("image") MultipartFile file
    ) throws IOException {
        imageService.uploadProductImage(file);
        return Result.success("成功");
    }


    @RequestMapping(method = RequestMethod.DELETE , value = "/Image")
    public Result deleteImageByUrl(String imageUrl){
        imageService.deleteImageByUrl(imageUrl);

        return Result.success("成功刪除");
    }



}
