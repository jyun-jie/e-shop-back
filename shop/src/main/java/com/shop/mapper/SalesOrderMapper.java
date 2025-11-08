package com.shop.mapper;

import com.shop.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SalesOrderMapper {

    @Select("SELECT * from e_shop.order where sellerId = #{userId} and state = 'To_Ship' ")
    List<Order> findOrderbyId(int userId) ;
}
