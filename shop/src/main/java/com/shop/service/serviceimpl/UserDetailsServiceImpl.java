package com.shop.service.serviceimpl;

import com.shop.dto.LoginDto;
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
    //查詢用戶
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查詢用戶訊息
        LoginDto user = userMapper.findByUsername(username);
        if(user == null){
            throw new RuntimeException("用戶名或密碼錯誤");
        }
        return user;
    }
}
