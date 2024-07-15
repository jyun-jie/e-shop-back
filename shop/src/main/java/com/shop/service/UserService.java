package com.shop.service;


import com.shop.entity.AuthenticationResponse;
import com.shop.dto.Login;

public interface UserService {

    Login findUserByUsername(String username);

    AuthenticationResponse authenticateIfUserExist(Login user);

    int findIdbyName();

    AuthenticationResponse registerIfVisitorNotExist(Login visitor);
    String findNamebyId(int id);

}
