package com.shop.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    String uploadProductImage(MultipartFile file) throws IOException;

    void deleteImageByUrl (String imageUrl)  ;


}
