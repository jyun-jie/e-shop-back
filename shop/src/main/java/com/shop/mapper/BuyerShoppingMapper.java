package com.shop.mapper;

import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BuyerShoppingMapper {

    @Select("select * from product limit #{pageNum} , #{pageSize} ")
    List<Product> selectProductPage(Integer pageNum , Integer pageSize);

    @Select("select * from product where id=#{id}")
    Product selectProductById(int id);
}
