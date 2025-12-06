package com.shop.service.serviceImpl;

import com.shop.dto.Login;
import com.shop.entity.AuthenticationResponse;
import com.shop.entity.UserLevel;
import com.shop.mapper.UserMapper;
import com.shop.service.JwtService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthenticationResponse registerIfVisitorNotExist(Login visitor){
        Login user = findUserByUsername(visitor.getUsername());
        if(user == null){
            return registerAndGetUsertoken(visitor,UserLevel.User);
        }
        return null;
    }

    @Override
    public Login findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }

    public AuthenticationResponse registerAndGetUsertoken(Login user,UserLevel userRole){
        registerUser(user,userRole);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void registerUser(Login user ,UserLevel userRole){
        userMapper.register(user.getUsername(), passwordEncoder.encode(user.getPassword()),userRole.toString());
    }

    public AuthenticationResponse  authenticateIfUserExist(Login visitor) {
        Login user = findUserByUsername(visitor.getUsername());
        if(user != null){
            return authenticateAndGetJwt(visitor);
        }
        return null;
    }

    public AuthenticationResponse authenticateAndGetJwt(Login visitor) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                visitor.getUsername(),
                visitor.getPassword())
        );
        visitor = findUserByUsername(visitor.getUsername());
        var jwtToken = jwtService.generateToken(visitor);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public int findIdbyName(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return userMapper.findIdbyName(username);
    }

    public String findNamebyId(int id){
        return userMapper.selectNameById(id);
    }

}
