package com.shop.mapper;

import com.shop.dto.OrderPaymentJoinDto;
import com.shop.entity.CartProduct;
import com.shop.entity.InOrderProduct;
import com.shop.entity.Order;
import com.shop.entity.OrderState;
import org.apache.ibatis.annotations.*;
import org.json.JSONObject;

import java.util.List;

@Mapper
public interface BuyerOrderMapper {

    @Insert("""
        INSERT INTO e_shop.`order` (master_order_id,userId,sellerId,state,create_time,total,
        receiverPhone,receiverAddress,postalName,receiverName,receiverEmail,postalAddress,
        delivery_type,pickup_store_id,pickup_store_name,pickupStoreType
    ) VALUES (#{masterOrderId},#{userId},#{sellerId},#{state},NOW(),#{total},
        #{receiverPhone},#{receiverAddress},#{postalName},#{receiverName},#{receiverEmail},#{postalAddress},
        #{deliveryType},#{pickupStoreId},#{pickupStoreName},#{pickupStoreType})""")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);

    @Insert("insert into e_shop.inorderproduct(orderId,product_Id,productName,price,quantity,create_Time,update_Time) values" +
            "(#{orderId},#{product.id},#{product.name},#{product.price},#{product.quantity},now(),now())")
    void insertInOrderProduct(CartProduct product,int orderId);


    @Select("select * from e_shop.order where userId = #{userId}")
    List<Order> selectNotPaidListByUserId(int userId);

    @Select("select * from e_shop.order where userId =#{userId} and state =#{type}")
    List<Order> selectListByUserIdAndState(int userId,String type);

    @Select("select * from e_shop.inorderproduct where orderId=#{orderId}")
    List<InOrderProduct> selectInOrderproductByOrderId(int orderId);

    @Update("update e_shop.order set state='Complete'  , completed_at = now() where id =#{orderId}")
    void changeStateToCompleted(int orderId);

    @Select("select * from e_shop.order where id = #{orderId}")
    Order selectByOrderId(int orderId);

    @Update("update product set quantity = #{quantity} where id = #{ProductId}")
    void updateQuantityByProductId(int ProductId, int quantity);

    @Update("update product set quantity = quantity - #{quantity} where id = #{ProductId} AND quantity >= #{quantity}")
    int shrinkStock(int ProductId , int quantity);

    @Update("update product set quantity = quantity + #{quantity} where id = #{ProductId}")
    void increaseStock(int ProductId, int quantity);

    @Update("update e_shop.order set state = #{state} where master_order_id = #{masterOrderId}")
    void updateStateByMasterOrderId(int masterOrderId, OrderState state);

    @Select("select * from e_shop.order where master_order_id = #{masterOrderId}")
    List<Order> selectByMasterOrderId(int masterOrderId);
    
    @Select("select * from e_shop.order where id = #{orderId}")
    Order selectOrderById(int orderId);
    
    @Update("update e_shop.order set logistics_order_id = #{logisticsOrderId} where id = #{orderId}")
    void updateLogisticsOrderId(int orderId, Integer logisticsOrderId);

    @Update("update e_shop.order set state = #{state} where id = #{orderId}")
    void updateOrderState(int orderId, OrderState state);



}

