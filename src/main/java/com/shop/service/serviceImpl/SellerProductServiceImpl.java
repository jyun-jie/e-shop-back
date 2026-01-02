package com.shop.service.serviceImpl;


import com.shop.dto.DelImageDto;
import com.shop.dto.HomeProductDto;
import com.shop.dto.ProductDetailDto;
import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import com.shop.entity.ProductImage;
import com.shop.entity.ProductPage;
import com.shop.entity.Seller;
import com.shop.mapper.ImageMapper;
import com.shop.mapper.SellerProductMapper;
import com.shop.service.ImageService;
import com.shop.service.SellerProductService;
import com.shop.service.SellerService;
import com.shop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedList;
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
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private SellerService sellerService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public  int insertProduct(ProductDto productDto , List<MultipartFile> images ,MultipartFile coverImage) throws IOException {
        Seller seller= sellerService.getActiveSellerOrThrow();
        Product product = new Product();

        product.setName(productDto.getName());
        product.setType(productDto.getType());
        product.setDescription(productDto.getDescription());
        product.setAddress(productDto.getAddress());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setSellerId(seller.getId());

        try {
             sellerProductMapper.insertProduct(product, seller.getId());
        }catch (Exception e){
            log.error("重復產品錯誤");
            throw new RuntimeException("重復產品");
        }

        int isSucess ;
        int productId = product.getId();
        String coverUrl = imageService.uploadProductImage(coverImage);
        isSucess =  imageMapper.insertCoverImage(productId , coverUrl ) ;
        if(isSucess == 0){
            log.error("新增封面圖片出現問題");
            throw new RuntimeException("新增封面圖片出現問題");
        }

        int order = 0 ;
        for(MultipartFile image : images){
            String imageUrl= imageService.uploadProductImage(image) ;

            isSucess = imageMapper.insertProductImage(
                    new ProductImage(productId,imageUrl, order++)
            );
            if(isSucess == 0){
                log.error("新增圖片出現問題");
                throw new RuntimeException("新增圖片出現問題");
            }
        }


        return 1;
    }


    @Override
    public List<ProductDetailDto> findProdcutDetailById(int id) {
        sellerService.getActiveSellerOrThrow();
        return  sellerProductMapper.selectProductDetailById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateProductById(ProductDto newProduct ,
                                 List<MultipartFile> newImages ,
                                 List<DelImageDto> delImages ,
                                 MultipartFile newCover
    ) throws IOException{
        //updateResult > 0 represent success
        sellerService.getActiveSellerOrThrow();
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

        int isSucess ;
        if (!(delImages.isEmpty())) {
            for (DelImageDto delImage : delImages) {
                System.out.println(delImage);

                imageService.deleteImageByUrl(delImage.getUrl());
                imageMapper.deleteImage(delImage.getUrl() , delImage.getId());
            }
        }

        if(newCover != null){
            String url = imageService.uploadProductImage(newCover);

            isSucess = imageMapper.insertCoverImage(product.getId() ,url );

            if(isSucess == 0){
                log.error("修改封面出現問題");
                throw new RuntimeException("修改封面出現問題");
            }
        }


        if (newImages != null) {
            int maxSort = sellerProductMapper.findMaxSortOrder(product.getId()) + 1;
            int nextSort = 0;
            if( maxSort != 0 ){
                nextSort = maxSort + 1 ;
            }

            for (MultipartFile img : newImages) {
                String url = imageService.uploadProductImage(img);

                isSucess = imageMapper.insertProductImage(
                        new ProductImage(product.getId() ,url , nextSort++)
                );

                if(isSucess == 0){
                    log.error("修改圖片出現問題");
                    throw new RuntimeException("修改圖片出現問題");
                }
            }
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteProductById(int id) {
        sellerService.getActiveSellerOrThrow();

        log.info("開始嘗試刪除商品 ID: {}", id);
        // use FOR UPDATE to lock the data in the column
        Product product= sellerProductMapper.selectProductForUpdate(id);


        if (product == null) {
            log.warn("商品 {} 不存在", id);
            throw new NoSuchElementException("找不到該商品，無法執行刪除");
        }

        if (product.getStatus().toString() != "taken_down") {
            log.info("商品 {} 還沒下架無法刪除", id);
            throw new RuntimeException("找不到該商品，無法執行刪除");
        }

        int result = sellerProductMapper.logicDeleteProduct(id);
        if (result == 0) {
            throw new RuntimeException("刪除商品失敗，請稍後再試");
        }
        imageMapper.markDeletedByProductId(id);



        log.info("商品 {} 邏輯刪除成功", id);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public ProductPage<HomeProductDto> findProductPage(Integer pageNum, Integer pageSize ,String status) {
        Seller seller= sellerService.getActiveSellerOrThrow();
        log.info("id : {} 有在seller裡 " , seller.getUserId());

        List<HomeProductDto> productList = sellerProductMapper.selectProductPageBySellerId(pageNum,pageSize,seller.getId() , status);
        int newPage = pageNum+pageSize;
        return new ProductPage<>(newPage ,productList);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public int takenDownProduct(int id) {
        sellerService.getActiveSellerOrThrow();
        log.info("開始嘗試下架商品 ID: {}", id);
        // use FOR UPDATE to lock the data in the column
        Product product= sellerProductMapper.selectProductForUpdate(id);

        if (product == null) {
            log.warn("商品 {} 不存在", id);
            throw new NoSuchElementException("找不到該商品，無法下架");
        }

        if (product.getStatus().toString() != "in_stock") {
            log.info("商品 {} 已經是下架狀態，無需重複執行", id);
            throw new RuntimeException("已經是下架狀態，無法下架");
        }

        int result = sellerProductMapper.takenDownProduct(id);
        if (result == 0) {
            throw new RuntimeException("下架商品失敗，請稍後再試");
        }

        log.info("商品 {} 下架成功", id);
        return result;
    }

}
