package com.shop.service;


import com.shop.dto.Login;
import com.shop.dto.SellerApplicationDto;
import com.shop.entity.AuthenticationResponse;

public interface UserService {

    AuthenticationResponse registerIfVisitorNotExist(Login visitor);

    Login findUserByUsername(String username);

    AuthenticationResponse authenticateIfUserExist(Login user);

    int findIdbyName();


    String findNamebyId(int id);

    Boolean applySeller(SellerApplicationDto req) ;

}
