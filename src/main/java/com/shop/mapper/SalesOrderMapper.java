package com.shop.mapper;

import com.shop.entity.Order;
import com.shop.entity.OrderState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SalesOrderMapper {

    @Select("SELECT * from e_shop.order where sellerId = #{sellerId} and state = #{orderState} ")
    List<Order> findOrderbyState(int sellerId , String orderState) ;


//    @Select("SELECT * from e_shop.order where sellerId = #{sellerId}")
//    List<Order> findNotPaidOrderbyId(int sellerId ) ;

    @Update("update e_shop.order set state = 'Shipping' where id = #{orderId}")
    void setStateToShipping(int orderId);

    @Select("SELECT * FROM e_shop.order " +
            "WHERE sellerId = #{sellerId} AND logistics_order_id IS NULL AND state = 'UNCHECKED' AND pickupStoreType = #{storeType}")
    List<Order> findNonCreatedLogisticsOrders(int sellerId , String storeType ) ;

    //測試 -0 天 需自行調整
    @Select("select sellerId , id , total from e_shop.order AS ord " +
            "where ord.completed_at <= #{now} - INTERVAL 0 DAY AND ord.state = 'Complete' " +
            "AND NOT EXISTS ( SELECT 1 FROM seller_payout AS sp where sp.orderId = ord.id)")
    List<Order> findCompletedAndNotPayout(LocalDateTime now) ;
}
