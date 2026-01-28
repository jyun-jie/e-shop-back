package com.shop.service.serviceImpl;

import com.shop.Exception.BusinessException;
import com.shop.dto.Login;
import com.shop.dto.SellerApplicationDto;
import com.shop.entity.AuthenticationResponse;
import com.shop.entity.User;
import com.shop.entity.UserRole;
import com.shop.mapper.UserMapper;
import com.shop.service.JwtService;
import com.shop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
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
    public AuthenticationResponse registerIfUserNotExist(Login visitor){
        Login user = findUserByUsername(visitor.getUsername());
        if(user == null){
            return registerAndGetUsertoken(visitor, UserRole.User);
        }
        return null;
    }

    @Override
    public Login findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }

    public AuthenticationResponse registerAndGetUsertoken(Login user, UserRole userRole){
        user.setRole(userRole);
        registerUser(user,userRole);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void registerUser(Login user , UserRole userRole){
        userMapper.register(user.getUsername(), passwordEncoder.encode(user.getPassword()),userRole.toString());
    }

    @Override
    public AuthenticationResponse  authenticateIfUserExist(Login visitor) {
        Login user = findUserByUsername(visitor.getUsername());
        if(user != null){
            return authenticateAndGetJwt(visitor);
        }
        System.out.println("無此人");
        return null;
    }

    public AuthenticationResponse authenticateAndGetJwt(Login visitor) {
        System.out.println("驗證");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                visitor.getUsername(),
                visitor.getPassword())
        );
        System.out.println("找人");
        visitor = findUserByUsername(visitor.getUsername());
        System.out.println("找到人");
        var jwtToken = jwtService.generateToken(visitor);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public int findIdbyName(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return userMapper.findIdbyName(username);
    }

    @Override
    public String findNamebyId(int id){
        User user = userMapper.selectById(id);
        return user.getUsername();
    }



    @Override
    public Boolean applySeller(SellerApplicationDto req) {
        int userId = findIdbyName();
        Integer isExist = userMapper.existPendingByUser(userId) ;

        if(isExist != null && isExist != 0){
            log.error("有申請過了，正在審核中");
            throw new BusinessException("已有申請過，正在審核中");
        }

        Integer isSucess = userMapper.applySeller(userId , req) ;
        if(isSucess == null && isSucess == 0 ){
            log.error("申請失敗");
            throw new RuntimeException("申請失敗，有其他錯誤");
        }

        return true;
    }
}
