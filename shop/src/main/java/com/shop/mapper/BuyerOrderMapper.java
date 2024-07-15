package com.shop.mapper;

import com.shop.entity.CartProduct;
import com.shop.entity.InOrderProduct;
import org.apache.ibatis.annotations.*;
import com.shop.entity.Order;

import java.util.List;

@Mapper
public interface BuyerOrderMapper {
    @Insert("insert into shop.order(userId,sellerId,state,createTime,total,receiverAddress" +
            ",postalName,receiverName,payment_method ) values (#{userId},#{sellerId},#{state}" +
            ",now(),#{total},#{receiverAddress}" +
            ",#{postalName},#{receiverName},#{payment_method})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);

    @Insert("insert into shop.InOrderProduct(orderId,product_Id,productName,price,quantity,createTime,updateTime) values" +
            "(#{orderId},#{product.id},#{product.name},#{product.price},#{product.quantity},now(),now())")
    void insertInOrderProduct(CartProduct product,int orderId);


    @Select("select * from shop.order where userId = #{userId} and isPay = false")
    List<Order> selectNotPaidListByUserId(int userId);

    @Select("select * from shop.order where userId =#{userId} and state =#{type}")
    List<Order> selectListByUserIdAndState(int userId,String type);

    @Select("select * from shop.inorderproduct where orderId=#{orderId}")
    List<InOrderProduct> selectInOrderproductByOrderId(int orderId);

    @Update("update order set state= 'To_Receive' where id =#{orderId}")
    void changeStateToReceived(int orderId);

    @Update("update shop.order set state='Complete' where id =#{orderId}")
    void changeStateToCompleted(int orderId);

    @Select("select * from shop.order where id = #{orderId}")
    Order selectByOrderId(int orderId);


}
