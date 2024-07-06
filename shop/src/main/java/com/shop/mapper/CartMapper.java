package com.shop.mapper;

import com.shop.dto.ProductDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.PathVariable;

@Mapper
public interface CartMapper {
    @Select("select * from product where id=#{id}")
    ProductDto selectProductById(int id);

}
