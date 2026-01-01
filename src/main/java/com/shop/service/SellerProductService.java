package com.shop.service;

import com.shop.dto.DelImageDto;
import com.shop.dto.HomeProductDto;
import com.shop.dto.ProductDetailDto;
import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import com.shop.entity.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SellerProductService {

    int insertProduct(ProductDto product , List<MultipartFile> file)throws IOException;

    List<ProductDetailDto> findProdcutDetailById(int id );

    int updateProductById(ProductDto product ,List<MultipartFile> newImages
            , List<DelImageDto> delImages) throws IOException;

    int deleteProductById(int id);

    ProductPage<HomeProductDto> findProductPage(Integer pageNum, Integer pageSize , String status);

    int takenDownProduct(int id);
}

