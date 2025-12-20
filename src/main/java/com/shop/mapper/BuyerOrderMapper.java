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
    @Insert("insert into e_shop.order(userId,sellerId,state,create_Time,total,receiverAddress" +
            ",postalName,receiverName,payment_method,isPay ) values (#{userId},#{sellerId},#{state}" +
            ",now(),#{total},#{receiverAddress}" +
            ",#{postalName},#{receiverName},#{payment_method},#{isPay})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);

    @Insert("insert into e_shop.InOrderProduct(orderId,product_Id,productName,price,quantity,create_Time,update_Time) values" +
            "(#{orderId},#{product.id},#{product.name},#{product.price},#{product.quantity},now(),now())")
    void insertInOrderProduct(CartProduct product,int orderId);


    @Select("select * from e_shop.order where userId = #{userId} and isPay <> 1")
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

    @Select("SELECT quantity from product where id = #{ProductId} and status = 'in_stock'")
    int getProductQuantity(int ProductId);

}
