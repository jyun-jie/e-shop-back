package com.shop.controller;

import com.shop.entity.AuthenticationResponse;
import com.shop.dto.LoginDto;
import com.shop.entity.Result;
import com.shop.entity.UserLevel;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    //register
    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public Result register(@RequestBody LoginDto user){
        LoginDto u = userService.findByUsername(user.getUsername());
        if(u == null){
            user.setRole(UserLevel.User);
            AuthenticationResponse usertoken = userService.register(user);
            return Result.success(usertoken);
        } else{
           return Result.error("已有該用戶");
        }
    }

    //login
    @RequestMapping(method = RequestMethod.POST,value = "/user")
    public Result login(@RequestBody LoginDto user){
        //查看是否有該username用戶
        LoginDto login = userService.findByUsername(user.getUsername());
        if(login == null){
            return Result.error("查無此用戶");
        }
        AuthenticationResponse usertoken = userService.authenticate(user);
        if(usertoken != null){
            return Result.success(usertoken);
        }
        return Result.error("密碼錯誤");
    }

    //logout


}
