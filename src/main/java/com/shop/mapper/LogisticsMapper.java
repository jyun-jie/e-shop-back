package com.shop.mapper;

import com.shop.dto.LogisticsCreateResponseDto;
import com.shop.dto.LogisticsOrderDto;
import com.shop.dto.LogisticsStatusQueryDto;
import com.shop.entity.LogisticsOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LogisticsMapper {
    
    @Insert("INSERT INTO logistics_order (" +
            "order_id, master_order_id, logistics_type, store_type ,merchantOrderNo , all_pay_logistics_id, " +
            "sellerId , sender_name, sender_phone, sender_cell_phone, " +
            "receiver_name, receiver_phone, receiver_cell_phone, receiver_email, " +
            "store_id, store_name, is_cod, cod_amount, " +
            "logistics_status, logistics_status_desc, create_time, update_time" +
            ") VALUES (" +
            "#{orderId}, #{masterOrderId}, #{logisticsType}, #{storeType}, #{merchantOrderNo},  #{allPayLogisticsId} ," +
            " #{sellerId} ,#{senderName}, #{senderPhone}, #{senderCellPhone}, " +
            "#{receiverName}, #{receiverPhone}, #{receiverCellPhone}, #{receiverEmail}, " +
            "#{storeId} , #{storeName}, #{isCod}, #{codAmount}, " +
            "#{logisticsStatus}, #{logisticsStatusDesc}, NOW(), NOW()" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(LogisticsOrder logisticsOrder);
    
    @Select("SELECT * FROM logistics_order WHERE id = #{id}")
    LogisticsOrder findById(Integer id);
    
    @Select("SELECT * FROM logistics_order WHERE order_id = #{orderId} , buyer = #{userId}")
    LogisticsOrder findByOrderId(Integer orderId , Integer userId);

    @Select("SELECT * FROM logistics_order WHERE merchantOrderNo = #{merchantOrderNo}")
    LogisticsOrder findByMerchantOrderNo(String merchantOrderNo);
    
    @Select("SELECT * FROM logistics_order WHERE master_order_id = #{masterOrderId}")
    List<LogisticsOrder> findByMasterOrderId(Integer masterOrderId);
    
    @Update("UPDATE logistics_order SET " +
            "logistics_status = #{logisticsStatusQueryDto.RetId}, " +
            "logistics_status_desc = #{logisticsStatusQueryDto.RetString}, " +
            "update_time = NOW() , " +
            "needStatusCheck = #{needCheck}  " +
            "WHERE merchantOrderNo = #{logisticsStatusQueryDto.MerchantOrderNo}")
    void updateStatus(LogisticsStatusQueryDto logisticsStatusQueryDto , Boolean needCheck);

    @Select("SELECT * FROM logistics_order WHERE store_type = #{storeType} AND sellerId = #{sellerId}")
    List<LogisticsOrderDto> getLogisticsOrderByStoreTypeAndUserId(String storeType , int sellerId) ;

    @Select("SELECT * FROM logistics_order WHERE needStatusCheck = true")
    List<LogisticsOrder> findNeedStatusCheck();
}
