package com.shop.service;

import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import com.shop.entity.ProductPage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SellerProductService {

    int insertProduct(ProductDto product , MultipartFile file)throws IOException;

    Product findProdcutById(int id );

    int updateProductById(int id ,Product newProduct);

    int deleteProductById(int id);

    ProductPage<Product> findProductPage(Integer pageNum, Integer pageSize);
}
