package com.shop.mapper;

import com.shop.entity.CartProduct;
import com.shop.entity.InOrderProduct;
import com.shop.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BuyerOrderMapper {
    /***
     * e_shop表資料庫名稱
     * order表資料表名稱
     *
     * ***/
    @Insert("insert into e_shop.order(master_order_id , userId,sellerId,state,create_Time,total,receiverAddress" +
            ",postalName,receiverName) values (#{master_order_id} , #{userId},#{sellerId},#{state}" +
            ",now(),#{total},#{receiverAddress}" +
            ",#{postalName},#{receiverName})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);

    @Insert("insert into e_shop.InOrderProduct(orderId,product_Id,productName,price,quantity,create_Time,update_Time) values" +
            "(#{orderId},#{product.id},#{product.name},#{product.price},#{product.quantity},now(),now())")
    void insertInOrderProduct(CartProduct product,int orderId);


    @Select("select * from e_shop.order where userId = #{userId}")
    List<Order> selectNotPaidListByUserId(int userId);

    @Select("select * from e_shop.order where userId =#{userId} and state =#{type}")
    List<Order> selectListByUserIdAndState(int userId,String type);

    @Select("select * from e_shop.inorderproduct where orderId=#{orderId}")
    List<InOrderProduct> selectInOrderproductByOrderId(int orderId);

    @Update("update e_shop.order set state='Complete' where id =#{orderId}")
    void changeStateToCompleted(int orderId);

    @Select("select * from e_shop.order where id = #{orderId}")
    Order selectByOrderId(int orderId);

    @Update("update product set quantity = #{quantity} where id = #{ProductId}")
    void updateQuantityByProductId(int ProductId, int quantity);

    @Update("update product set quantity = quantity - #{quantity} where id = #{ProductId} AND quantity >= #{quantity}")
    int shrinkStock(int ProductId , int quantity);

}
