package com.shop.mapper;

import com.shop.entity.MasterOrder;
import com.shop.entity.Order;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MasterOrderMapper {
    @Insert("insert into e_shop.master_order(buyer_id,total_amount,payment_status,pay_method,created_at ) " +
            "values (#{buyer_id},#{total_amount},'INIT' " +
            ",#{pay_method},now())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertMasterOrder(MasterOrder masterOrder);

    @Select("SELECT * FROM e_shop.master_order WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id", id = true),
            @Result(property = "buyer_id", column = "buyer_id"),
            @Result(property = "total_amount", column = "total_amount"),
            @Result(property = "payment_status", column = "payment_status"),
            @Result(property = "pay_method", column = "pay_method"),
            @Result(property = "created_at", column = "created_at")
    })
    MasterOrder findById(int masterOrderId) ;

    @Update("UPDATE e_shop.master_order SET payment_status = #{status} WHERE id = #{masterOrderId}")
    void updateStatus(int masterOrderId, String status);

    @Select("SELECT * FROM e_shop.master_order " +
            "WHERE payment_status = 'INIT' AND pay_method !='COD' AND created_at < #{timeoutThreshold}")
    @Results({
            @Result(property = "id", column = "id", id = true),
            @Result(property = "buyer_id", column = "buyer_id"),
            @Result(property = "total_amount", column = "total_amount"),
            @Result(property = "payment_status", column = "payment_status"),
            @Result(property = "pay_method", column = "pay_method"),
            @Result(property = "created_at", column = "created_at")
    })
    List<MasterOrder> selectExpiredUnpaidOrders(LocalDateTime timeoutThreshold);
}
