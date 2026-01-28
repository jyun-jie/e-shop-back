package com.shop.mapper;

import com.shop.entity.ProductImage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.multipart.MultipartFile;

@Mapper
public interface ImageMapper {

    @Update("UPDATE product_image set status = 'delete' where productId = #{productId}")
    int markDeletedByProductId(int productId ) ;

    @Insert("insert into product_image(productId , imageUrl , sort_order ,created_at)values " +
            "(#{productId} , #{imageUrl} , #{sort_order} , now())")
    int insertProductImage(ProductImage productImage);


    @Insert("insert into product_image(productId , imageUrl  , sort_order,created_at , image_type )values " +
            "(#{productId} , #{coverUrl} , 0 ,  now() , 'cover') ")
    int insertCoverImage(int productId , String coverUrl) ;

    @Delete("delete from product_image where imageUrl = #{url} AND id = #{id}")
    void deleteImage(String url , int id);
}
