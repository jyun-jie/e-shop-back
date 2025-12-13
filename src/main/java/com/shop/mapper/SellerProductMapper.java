package com.shop.mapper;

import com.shop.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SellerProductMapper {

    @Select("select * from product where id=#{id}")
    Product selectProductById(int id);

    @Insert("insert into product (id,name , type,description" +
            ",imageUrl,address,price,quantity,sellerId)" +
            "values(#{product.id}, #{product.name}, #{product.type}, #{product.description}, #{product.imageUrl}, #{product.address}, #{product.price}, #{product.quantity}, #{sellerId})")
    int insertProduct(int sellerId , Product product);

    @Update("update product set name=#{product.name},type=#{product.type},description=#{product.description}" +
            ",imageUrl=#{product.imageUrl},address=#{product.address},price=#{product.price},quantity=#{product.quantity}" +
            " where id=#{id}")
    int updateProduct(int id ,Product product);

    @Delete("delete from product where id=#{id}")
    int deleteProduct(int id);

    @Select("select * from product where sellerId=#{sellerId} limit #{pageNum} , #{pageSize} ")
    List<Product> selectProductPageBySellerId(Integer pageNum , Integer pageSize ,  int sellerId);



}
