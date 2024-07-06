package com.shop.service;


import com.shop.entity.AuthenticationResponse;
import com.shop.dto.LoginDto;

public interface UserService {

    LoginDto findByUsername(String username);

    AuthenticationResponse register(LoginDto user);

    AuthenticationResponse authenticate(LoginDto user);

    int findIdbyName();

}
