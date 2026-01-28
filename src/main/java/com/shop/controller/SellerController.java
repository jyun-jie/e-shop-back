package com.shop.controller;

import com.shop.dto.PayoutDto;
import com.shop.mapper.SellerMapper;
import com.shop.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(value = "seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;


    @RequestMapping(value = "payout")
    @PreAuthorize("hasRole('User')")
    public PayoutDto showMonthPayout(){
        PayoutDto payoutInfo = sellerService.showMonthPayout();
        return payoutInfo ;
    }




}
