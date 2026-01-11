package com.shop.mapper;

import com.shop.dto.HomeProductDto;
import com.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BuyerShoppingMapper {


    @Select("select * from product where id=#{id}")
    Product selectProductById(int id);

    @Select("SELECT p.id , p.name , p.price , p.rate , p.address , pi.imageUrl " +
            "From product as p  " +
            "LEFT JOIN product_image as pi " +
            "ON p.id = pi.productId AND image_type = 'cover' " +
            "where  p.status = 'in_stock' AND quantity <> 0   limit #{pageNum} , #{pageSize} ")
    List<HomeProductDto> selectProductPage(Integer pageNum , Integer pageSize );
}
