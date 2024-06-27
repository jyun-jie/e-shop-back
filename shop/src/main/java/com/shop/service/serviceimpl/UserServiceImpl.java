package com.shop.service.serviceimpl;
import com.shop.config.AuthenticationResponse;
import com.shop.dto.LoginDto;
import com.shop.entity.Result;
import com.shop.entity.User;
import com.shop.mapper.UserMapper;
import com.shop.service.JwtService;
import com.shop.service.UserService;
import io.lettuce.core.ScriptOutputType;
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
    public LoginDto findByUsername(String username) {
        LoginDto u = userMapper.findByUsername(username);
        return u;
    }

    public int findIdbyName(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user = userDetails.getUsername();
        return userMapper.findIdbyName(user);
    }

    //註冊

    public AuthenticationResponse register(LoginDto user){
        userMapper.register(user.getUsername(),passwordEncoder.encode(user.getPassword()),user.getRole().toString());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    //登入
    public AuthenticationResponse authenticate(LoginDto user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword())
        );
        user = userMapper.findByUsername(user.getUsername());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


}
