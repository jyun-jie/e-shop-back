package com.shop.service.serviceImpl;


import com.shop.dto.DelImageDto;
import com.shop.dto.HomeProductDto;
import com.shop.dto.ProductDetailDto;
import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import com.shop.entity.ProductImage;
import com.shop.entity.ProductPage;
import com.shop.mapper.SellerProductMapper;
import com.shop.service.ImageService;
import com.shop.service.SellerProductService;
import com.shop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class SellerProductServiceImpl implements SellerProductService {


    @Autowired
    SellerProductMapper sellerProductMapper;

    @Autowired
    ImageService imageService ;

    @Autowired
    UserService userService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public  int insertProduct(ProductDto productDto , List<MultipartFile> images) throws IOException {
        int sellerId = userService.findIdbyName();
        Product product = new Product();

        product.setName(productDto.getName());
        product.setType(productDto.getType());
        product.setDescription(productDto.getDescription());
        product.setAddress(productDto.getAddress());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setSellerId(sellerId);

        sellerProductMapper.insertProduct( product ,sellerId );
        int productId = product.getId();

        int order = 0 ;
        for(MultipartFile image : images){
            String imageUrl= imageService.uploadProductImage(image) ;

            int isSucess = sellerProductMapper.insertProductImage(
                    new ProductImage(productId,imageUrl, order++)
            );
            if(isSucess == 0){
                log.error("新增圖片出現問題");
                throw new RuntimeException("新增圖片出現問題");
            }
        }


        return 1;
    }


    public List<ProductDetailDto> findProdcutDetailById(int id) {
        return  sellerProductMapper.selectProductDetailById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateProductById(ProductDto newProduct ,
                                 List<MultipartFile> newImages ,
                                 List<DelImageDto> delImages) throws IOException{
        //updateResult > 0 represent success
        log.info("開始嘗試修改商品 ID: {}", newProduct);
        Product product= sellerProductMapper.selectProductForUpdate(newProduct.getId());

        if (product == null) {
            log.warn("商品 {} 不存在", product.getId());
            throw new NoSuchElementException("找不到該商品，無法執行刪除");
        }

        int result = sellerProductMapper.updateProduct(product);
        if (result == 0) {
            throw new RuntimeException("修改商品失敗，請稍後再試");
        }

        if (delImages != null) {
            for (DelImageDto delImage : delImages) {

                deleteImageByUrl(delImage.getUrl());
                sellerProductMapper.deleteImage(delImage.getUrl() , delImage.getId());
            }
        }


        if (newImages != null) {
            int maxSort = sellerProductMapper.findMaxSortOrder(product.getId()) + 1;
            int nextSort = 0;
            if( maxSort != 0 ){
                nextSort = maxSort + 1 ;
            }

            for (MultipartFile img : newImages) {
                System.out.println(img);


                String url = imageService.uploadProductImage(img);


                int isSucess = sellerProductMapper.insertProductImage(
                        new ProductImage(product.getId() ,url , nextSort++)
                );

                if(isSucess == 0){
                    log.error("更新圖片出現問題");
                    throw new RuntimeException("更新圖片出現問題");
                }
            }
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteProductById(int id) {
        log.info("開始嘗試刪除商品 ID: {}", id);
        // use FOR UPDATE to lock the data in the column
        Product product= sellerProductMapper.selectProductForUpdate(id);

        if (product == null) {
            log.warn("商品 {} 不存在", id);
            throw new NoSuchElementException("找不到該商品，無法執行刪除");
        }

        if (product.getStatus() == "take_down") {
            log.info("商品 {} 已經是刪除狀態，無需重複執行", id);
            throw new RuntimeException("找不到該商品，無法執行刪除");
        }

        int result = sellerProductMapper.logicDeleteProduct(id);
        if (result == 0) {
            throw new RuntimeException("刪除商品失敗，請稍後再試");
        }

        log.info("商品 {} 邏輯刪除成功", id);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public ProductPage<HomeProductDto> findProductPage(Integer pageNum, Integer pageSize) {
        int sellerId = userService.findIdbyName();
        List<HomeProductDto> productList = sellerProductMapper.selectProductPageBySellerId(pageNum,pageSize,sellerId);
        int newPage = pageNum+pageSize;
        return new ProductPage<>(newPage ,productList);
    }

    @Transactional(readOnly = true)
    @Override
    public void deleteImageByUrl(String imageUrl) {
        imageService.deleteImageByUrl(imageUrl);
    }
}
