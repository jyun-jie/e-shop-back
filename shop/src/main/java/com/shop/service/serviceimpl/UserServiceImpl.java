package com.shop.service.serviceimpl;
import com.shop.entity.AuthenticationResponse;
import com.shop.dto.Login;
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

    @Override
    public Login findUserByUsername(String username) {
        Login user = userMapper.findUserByUsername(username);
        return user;
    }

    public int findIdbyName(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return userMapper.findIdbyName(username);
    }

    //註冊

    public AuthenticationResponse register(Login user){
        userMapper.register(user.getUsername(),passwordEncoder.encode(user.getPassword()),user.getRole().toString());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    //登入
    public AuthenticationResponse authenticate(Login loginer) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginer.getUsername(),
                loginer.getPassword())
        );
        loginer = userMapper.findUserByUsername(loginer.getUsername());
        var jwtToken = jwtService.generateToken(loginer);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


}
