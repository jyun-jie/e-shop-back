package com.shop.service;

import com.shop.dto.Login;
import com.shop.mapper.UserMapper;
import com.shop.service.serviceimpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;


@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserMapper userMapper;
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;

    Login visitor;

    @BeforeEach
    void setup(){
        visitor = new Login("helloaaa","123456");
    }

//    @Test
//    void UserService_registerIfVisitorNotExist_returnAuthenticationResponse(){
//        AuthenticationResponse token = AuthenticationResponse.builder().token("456789").build();
//
//        Mockito.when(userMapper.findUserByUsername(visitor.getUsername())).thenReturn(null);
//        Mockito.when(userService.registerAndGetUsertoken(visitor, UserLevel.User)).thenReturn(token);
//        Mockito.when(jwtService.generateToken(visitor));
//
//
//        AuthenticationResponse  actualResponse= userService.registerIfVisitorNotExist(visitor);
//
//        Assertions.assertEquals("456789",actualResponse.getToken());
//
//    }


}
