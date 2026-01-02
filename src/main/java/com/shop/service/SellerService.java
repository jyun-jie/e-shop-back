package com.shop.service;

import com.shop.Exception.AccessDeniedException;
import com.shop.entity.Seller;

public interface SellerService {
    Seller getActiveSellerOrThrow();

}
