package com.shop.mapper;

import com.shop.entity.Payment;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PaymentMapper {

    @Insert(" Insert into payment( master_order_id , trade_no ,amount ,pay_status) " +
            "values (  #{master_order_id} , #{trade_no} , #{amount} , " +
            "#{pay_status}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id" ,keyColumn = "id")
    void insert(Payment payment) ;

    @Select("SELECT * from payment where trade_no =#{tradeNo} FOR UPDATE")
    @Results({
            @Result(property = "id", column = "id", id = true),
            @Result(property = "master_order_id", column = "master_order_id"),
            @Result(property = "trade_no", column = "trade_no"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "pay_status", column = "pay_status"),
            @Result(property = "payTime", column = "created_at")
    })
    Payment findByTradeNoForUpdate(String tradeNo);

    @Update("update payment set pay_status = #{status} where id = #{paymentId}")
    void updateStatus(int paymentId,String status);

    @Select("SELECT trade_no from payment where master_order_id = #{masterOrderId}")
    String findTradeNoByMasterOrderId(int masterOrderId);
}
