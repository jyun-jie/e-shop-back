package com.shop.service;

import com.shop.dto.Login;
import com.shop.entity.AuthenticationResponse;
import com.shop.mapper.UserMapper;
import com.shop.service.serviceimpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;


@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserMapper userMapper;
    @Mock
    JwtService jwtService;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    PasswordEncoder passwordEncoder;

    Login visitor;

    String mockJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxd2UxMjM0NTYiLCJpYXQiOjE3MjUxODMzNjEsImV4cCI6MTcyNTE4NTE2MX0.yfplHM6PSEGKn3qGC7IJ-bQloOlD1oR_KcyRc3m-Vfg";
    AuthenticationResponse mockResponse = AuthenticationResponse.builder()
            .token(mockJwtToken)
            .build();


    @BeforeEach
    void setup(){
        visitor = new Login("qwe123456","123456");
    }

//    @Test
//    void UserService_registerIfVisitorNotExist_whenVisitorisNull_returnAuthenticationResponse(){
//
//        //需要聲明jwt 機制以執行測試
//
//
//        Mockito.when(jwtService.generateToken(visitor)).thenReturn(mockJwtToken);
//        Mockito.when(userService.findUserByUsername(visitor.getUsername())).thenReturn(null);
//        Mockito.when(userService.registerAndGetUsertoken(visitor, UserLevel.User)).thenReturn(mockResponse);
//
//
//        Login visitorResponse = userService.findUserByUsername(visitor.getUsername());
//        AuthenticationResponse authenticationResponse = userService.registerAndGetUsertoken(visitor,UserLevel.User);
//
//
//        Assertions.assertEquals(null,visitorResponse);
//        Assertions.assertEquals(mockJwtToken,jwtService.generateToken(visitor));
//        Assertions.assertEquals(mockJwtToken,authenticationResponse.getToken());
//
//
//
//    }



}
