package com.shop.mapper;

import com.shop.dto.PayoutDto;
import com.shop.entity.Seller;
import com.shop.entity.WasCalculatePayout;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface SellerMapper {

    @Select("select id from seller where userId = #{userId} AND status = 'active' ")
    Seller getActiveSellerOrThrow(int userId);

    @Select("select * from seller_payout where sellerId = #{sellerId}")
    PayoutDto getMonthPayoutInfo(int sellerId);

    @Insert("insert into seller_payout( sellerId , orderId , amount , payoutStatus , available_at , paid_at)" +
            "values (#{sellerId} , #{orderId} ,#{amount} , #{payoutStatus} , #{available_at},#{paid_at} )")
    @Options(useGeneratedKeys = true , keyProperty = "id")
    int insertMonthPayout(PayoutDto payoutDto);


}
