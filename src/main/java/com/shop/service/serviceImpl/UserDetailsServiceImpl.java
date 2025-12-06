package com.shop.service.serviceImpl;

import com.shop.dto.Login;
import com.shop.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查詢用戶訊息
        Login user = userMapper.findUserByUsername(username);
        if(user == null){
            throw new RuntimeException("用戶名或密碼錯誤");
        }
        return user;
    }
}