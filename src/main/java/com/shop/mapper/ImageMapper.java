package com.shop.mapper;

import com.shop.entity.ProductImage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ImageMapper {

    @Update("UPDATE product_image set status = 'delete' where productId = #{productId}")
    int markDeletedByProductId(int productId ) ;

    @Insert("insert into product_image(productId , imageUrl , sort_order ,created_at)values " +
            "(#{productId} , #{imageUrl} , #{sort_order} , now())")
    int insertProductImage(ProductImage productImage);

    @Delete("delete from product_image where imageUrl = #{url} AND id = #{id}")
    void deleteImage(String url , int id);
}
