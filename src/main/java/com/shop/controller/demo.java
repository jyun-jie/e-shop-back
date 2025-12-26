package com.shop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin
public class demo {

    @RequestMapping ("/Read/unAuth/Pro/xxx")
    public String demo(){
        //System.out.println("pay ");
        log.info("pay sucess");
        return "付款成功 ";
    }


    @PreAuthorize("hasRole('Seller')")
    @RequestMapping ("/seller")
    public String seller(){
        log.info("進來");
        return "Seller ";
    }
}
