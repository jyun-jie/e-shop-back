package com.shop.service;

import com.shop.dto.Login;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    //從jwt token 提取用戶名
    public String extractUsername(String token);

    //提取JWT令牌中的任何聲明（Claims），並通過提供的Function來解析它們。
    // 在payload上有三種聲明(Claims) reserved ,public,private，
    //註冊聲明的參數有iss、sub、aud、exp、nbf、iat、jti
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    public String generateToken(Login user);

    public String generateToken(
            Map<String, Object> extraClaims,
            Login user
    );

    public boolean isTokenValid(String token, UserDetails userDetails);



}
