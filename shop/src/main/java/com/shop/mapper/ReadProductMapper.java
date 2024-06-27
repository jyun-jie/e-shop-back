package com.shop.mapper;

import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReadProductMapper {

    @Select("select * from product limit #{pageNum} , #{pageSize} ")
    List<Product> loadPro(Integer pageNum , Integer pageSize);

    @Select("select * from product where id=#{id}")
    ProductDto selectProById(int id);
}
