package com.shop.mapper;

import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import org.apache.ibatis.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper
public interface SellerProductMapper {

    @Select("select * from product where id=#{id}")
    Product selectProductById(int id);

    @Insert("insert into product (id, name , type,description" +
            ",imageUrl,address,price,quantity,sellerId)" +
            "values(#{product.id}, #{product.name}, #{product.type}, #{product.description}, #{imageUrl}, #{product.address}, #{product.price}, #{product.quantity}, #{sellerId})")
    int insertProduct(int sellerId , ProductDto product , String imageUrl);

    @Update("update product set name=#{product.name},type=#{product.type},description=#{product.description}" +
            ",imageUrl=#{product.imageUrl},address=#{product.address},price=#{product.price},quantity=#{product.quantity}" +
            " where id=#{id}")
    int updateProduct(int id ,Product product);

    // 1️⃣ 使用 FOR UPDATE 鎖定該行資料
    @Select("SELECT * FROM product WHERE id = #{id} FOR UPDATE")
    Product selectProductForUpdate(int id);

    @Update("UPDATE product set status = 'taken_down' where id =#{id} and status = 'in_stock'")
    int logicDeleteProduct(int id);



    @Select("select * from product where sellerId=#{sellerId} limit #{pageNum} , #{pageSize} ")
    List<Product> selectProductPageBySellerId(Integer pageNum , Integer pageSize ,  int sellerId);



}
