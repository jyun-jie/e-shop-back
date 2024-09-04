package com.shop.service.serviceimpl;

import com.shop.dto.Login;
import com.shop.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
* 進行簽署token 製作的實現類
* */
@Service
public class JwtServiceImpl implements JwtService {
    private static final String SECRETKEY ="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @Override
    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Login user) {
        return generateToken(new HashMap<>(), user);

    }

    //簽署token
    public String generateToken(Map<String, Object> extraClaims,
                                Login user) {
        return Jwts
                .builder()
                //claims參數設置
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //token 期限
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*30))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //驗證Token有效性，比對JWT和UserDetails的Username(Email)是否相同
    //且不過期
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                //驗證聲明(放入解碼密鑰)
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getSignInKey() {
        //解密
        byte[] keyBytes = Decoders.BASE64.decode(SECRETKEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
