package com.shop.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class demo {

    @RequestMapping ("/Read/unAuth/Pro/xxx")
    public String demo(){
        //System.out.println("pay ");
        return "付款成功 ";
    }


    @PreAuthorize("hasRole('SELLER')")
    @RequestMapping ("/seller")
    public String seller(){
        System.out.println("進來");
        return "Seller ";
    }
}
