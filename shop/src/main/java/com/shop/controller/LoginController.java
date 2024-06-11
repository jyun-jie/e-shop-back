package com.shop.controller;

import com.shop.config.AuthenticationResponse;
import com.shop.dto.LoginDto;
import com.shop.entity.Result;
import com.shop.entity.Role;
import com.shop.entity.User;
import com.shop.mapper.UserMapper;
import com.shop.service.JwtService;
import com.shop.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    //register
    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public Result register(@RequestBody LoginDto user){
        LoginDto u = userService.findByUsername(user.getUsername());
        if(u == null){
            user.setRole(Role.User);
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


}
