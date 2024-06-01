//package com.shop.controller;
//
//import com.shop.dto.LoginDto;
//import com.shop.dto.UserDto;
//import com.shop.entity.Result;
//import com.shop.entity.User;
//import com.shop.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.File;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/user")
//public class UserController {
//
//    @Autowired
//    UserService userService;
//
//    //更新頭像
////    public Result update_Pic(@RequestParam(value = "file")File file){
////        userService.updatePic(file);
////    }
//    //修改密碼
//    @RequestMapping(method = RequestMethod.POST, value = "/pwd")
//    public Result update_pwd(@RequestBody UserDto user ,String token){
//        if( !(user.getOldpassword()==null)|| !(user.getNewpassword()==null) || !(user.getRenewpassword()==null)){
//            return Result.error("缺少資料");
//        }
//    }
//
//
//    //詳情資料
//}
