package com.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class demo {

    @RequestMapping ("/Read")
    public String demo(){
        System.out.println("pay failed ");
        return "付款成功 ";
    }
}
