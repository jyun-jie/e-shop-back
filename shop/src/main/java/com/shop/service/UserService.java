package com.shop.service;


import com.shop.config.AuthenticationResponse;
import com.shop.dto.LoginDto;
import com.shop.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    LoginDto findByUsername(String username);

    AuthenticationResponse register(LoginDto user);

    AuthenticationResponse authenticate(LoginDto user);

    String loadUser();

}
