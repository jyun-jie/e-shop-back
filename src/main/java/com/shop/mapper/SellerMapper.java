package com.shop.mapper;

import com.shop.entity.Seller;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface SellerMapper {

    @Select("select id from seller where userId = #{userId} AND status = 'active' ")
    Seller getActiveSellerOrThrow(int userId);
}
