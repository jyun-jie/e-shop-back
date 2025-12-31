package com.shop.service;

import com.shop.dto.HomeProductDto;
import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SellerProductService {

    int insertProduct(ProductDto product , List<MultipartFile> file)throws IOException;

    Product findProdcutById(int id );

    int updateProductById(int id ,Product newProduct);

    int deleteProductById(int id);

    ProductPage<HomeProductDto> findProductPage(Integer pageNum, Integer pageSize);
}
