package com.shop.mapper;

import com.shop.dto.ProductDto;
import com.shop.entity.ProPage;
import com.shop.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SellProductMapper {


    //查看該產品詳細資料
    @Select("select * from product where id=#{id}")
    ProductDto selectProById(int id);

    //新增產品
    @Insert("insert into product (id,name , type,description" +
            ",imageUrl,address,price,quantity,sellerId)" +
            "values(#{product.id}, #{product.name}, #{product.type}, #{product.description}, #{product.imageUrl}, #{product.address}, #{product.price}, #{product.quantity}, #{sellerId})")
    int insert(int sellerId , Product product);
    //(name , type, description,imageUrl,address,price,quantity,rating)
    //更新產品
    @Update("update product set name=#{product.name},type=#{product.type},description=#{product.description}" +
            ",imageUrl=#{product.imageUrl},address=#{product.address},price=#{product.price},quantity=#{product.quantity}" +
            " where id=#{id}")
    int update(int id ,Product product);
    //刪除產品
    @Delete("delete from product where id=#{id}")
    int delete(int id);


    //分頁獲取產品
    @Select("select * from product where sellerId=#{sellerId} limit #{pageNum} , #{pageSize} ")
    List<Product> loadPro(Integer pageNum , Integer pageSize ,  int sellerId);

}
