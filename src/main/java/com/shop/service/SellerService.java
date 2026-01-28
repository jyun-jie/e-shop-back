package com.shop.service;

import com.shop.Exception.AccessDeniedException;
import com.shop.dto.PayoutDto;
import com.shop.entity.Seller;

public interface SellerService {
    Seller getActiveSellerOrThrow();

    PayoutDto showMonthPayout();

}
