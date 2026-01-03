package com.shop.mapper;

import com.shop.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SalesOrderMapper {

    @Select("SELECT * from e_shop.order where sellerId = #{sellerId} and state = #{orderState} ")
    List<Order> findOrderbyId(int sellerId , String orderState) ;

    @Select("SELECT * from e_shop.order where sellerId = #{sellerId}")
    List<Order> findNotPaidOrderbyId(int sellerId ) ;

    @Update("update e_shop.order set state = 'Shipping' where id = #{orderId}")
    void setStateToShipping(int orderId);
}
