package com.shop.service;


import com.shop.entity.AuthenticationResponse;
import com.shop.dto.Login;

public interface UserService {

    Login findUserByUsername(String username);

    AuthenticationResponse register(Login user);

    AuthenticationResponse authenticate(Login user);

    int findIdbyName();

}
