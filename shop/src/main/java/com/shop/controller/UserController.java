package com.shop.controller;

import com.shop.dto.Login;
import com.shop.entity.AuthenticationResponse;
import com.shop.entity.Result;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/login")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public Result register(@RequestBody Login visitor){
        AuthenticationResponse userToken =  userService.registerIfVisitorNotExist(visitor);
        if (userToken != null) {
            return Result.success(userToken);
        }
        return Result.error("已有此用戶");
    }

    //login
    @RequestMapping(method = RequestMethod.POST,value = "/user")
    public Result login(@RequestBody Login visitor){
        AuthenticationResponse usertoken = userService.authenticateIfUserExist(visitor);
        if(usertoken != null){
            return Result.success(usertoken);
        }
        return Result.error("查無此用戶或密碼錯誤");
    }

    //logout



}
