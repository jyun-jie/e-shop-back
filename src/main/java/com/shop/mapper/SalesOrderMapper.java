package com.shop.mapper;

import com.shop.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SalesOrderMapper {

    @Select("SELECT * from e_shop.order where sellerId = #{userId} and state = #{orderState} ")
    List<Order> findOrderbyId(int userId , String orderState) ;

    @Select("SELECT * from e_shop.order where sellerId = #{userId} and isPay = '0' ")
    List<Order> findNotPaidOrderbyId(int userId ) ;

    @Update("update e_shop.order set state = 'Shipping' where id = #{orderId}")
    void setStateToShipping(int orderId);
}
