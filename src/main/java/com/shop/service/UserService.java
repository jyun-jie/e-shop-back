package com.shop.service;


import com.shop.dto.Login;
import com.shop.entity.AuthenticationResponse;

public interface UserService {

    Login findUserByUsername(String username);

    AuthenticationResponse registerIfVisitorNotExist(Login visitor);


}
