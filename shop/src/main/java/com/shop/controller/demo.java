package com.shop.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class demo {

    @RequestMapping ("/Read")
    public String demo(){
        System.out.println("hello");
        return "hello";
    }

    @RequestMapping ("/seller")
    public String sell(){

        return "seller";
    }
}
