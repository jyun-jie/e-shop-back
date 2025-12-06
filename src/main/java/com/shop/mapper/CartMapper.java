package com.shop.mapper;

import com.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CartMapper {

    @Select("select * from product where id=#{id}")
    Product selectProductById(int id);

}
