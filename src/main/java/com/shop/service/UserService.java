package com.shop.service;


import com.shop.dto.Login;
import com.shop.entity.AuthenticationResponse;

public interface UserService {

    Login findUserByUsername(String username);

    AuthenticationResponse authenticateIfUserExist(Login user);

    int findIdbyName();

    AuthenticationResponse registerIfVisitorNotExist(Login visitor);
    String findNamebyId(int id);

}
