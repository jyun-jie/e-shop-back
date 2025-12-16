package com.shop.mapper;

import com.shop.entity.Payment;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PaymentMapper {

    @Insert(" Insert into payment(master_order_id , trade_no ,amount ,pay_status) " +
            "values (#{master_order_id} , #{trade_no} , #{amount} , " +
            "#{pay_status}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Payment payment) ;

    @Select("SELECT * from payment where trade_no =#{tradeNo} ")
    Payment findByTradeNo(String tradeNo);

    @Update("update payment set pay_status = #{status} where id = #{paymentId}")
    void updateStatus(int paymentId,String status);
}
