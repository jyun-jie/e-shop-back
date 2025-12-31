package com.shop.mapper;

import com.shop.dto.HomeProductDto;
import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import com.shop.entity.ProductImage;
import org.apache.ibatis.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper
public interface SellerProductMapper {

    @Select("select * from product where id=#{id}")
    Product selectProductById(int id);

    @Insert("insert into product ( name , type,description" +
            ",address,price,quantity,sellerId)" +
            "values( #{product.name}, #{product.type}, #{product.description}, #{product.address}, #{product.price}, #{product.quantity}, #{sellerId})")
    @Options(useGeneratedKeys = true, keyProperty = "product.id")
    int insertProduct(@Param("product") Product product , int sellerId );

    @Update("update product set name=#{product.name},type=#{product.type},description=#{product.description}" +
            ",imageUrl=#{product.imageUrl},address=#{product.address},price=#{product.price},quantity=#{product.quantity}" +
            " where id=#{id}")
    int updateProduct(int id ,Product product);

    // 1️⃣ 使用 FOR UPDATE 鎖定該行資料
    @Select("SELECT * FROM product WHERE id = #{id} FOR UPDATE")
    Product selectProductForUpdate(int id);

    @Update("UPDATE product set status = 'taken_down' where id =#{id} and status = 'in_stock'")
    int logicDeleteProduct(int id);



    @Select("SELECT p.id , p.name , p.price , p.rate , p.address , pi.imageUrl " +
            "From product as p  " +
            "LEFT JOIN product_image as pi " +
            "ON p.id = pi.productId AND pi.sort_order = 0 " +
            "where p.sellerId = #{sellerId} limit #{pageNum} , #{pageSize} ")
    List<HomeProductDto> selectProductPageBySellerId(Integer pageNum , Integer pageSize , int sellerId);

    @Insert("insert into product_image(productId , imageUrl , sort_order ,created_at)values " +
            "(#{productId} , #{imageUrl} , #{sort_order} , now())")
    int insertProductImage(ProductImage productImage);

}
