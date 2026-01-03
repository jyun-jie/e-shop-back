package com.shop.service.serviceImpl;

import com.shop.Exception.AccessDeniedException;
import com.shop.entity.Seller;
import com.shop.mapper.SellerMapper;
import com.shop.service.SellerService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private UserService userService;
    @Autowired
    private SellerMapper sellerMapper;

    @Override
    public Seller getActiveSellerOrThrow(){
        int userId = userService.findIdbyName();
        Seller seller = sellerMapper.getActiveSellerOrThrow(userId);
        if(seller == null  ){
            throw new AccessDeniedException("seller") ;

        }
        return seller ;
    }
}
