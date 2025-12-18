package com.shop.filter;

import com.shop.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        //從HEADER.authorization 中獲取token
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        //達成下面兩條件之一 就不對jwt做後續操作直接return
        System.out.println(authHeader);

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        jwt=authHeader.substring(7);//取Bearer 後的 token
        username = jwtService.extractUsername(jwt);//提取token 中的用戶名

        //如果用戶名!=null 且 securityContext中不存在身分驗證就做
        if(username !=null && SecurityContextHolder.getContext().getAuthentication()==null){
            //使用UserDetailsService根據用戶名  加載用戶詳細信息。
            //解耦 數據與業務邏輯
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails) ){
                //userdetails 會提取 jwt 中所有claims內容
                //確認username = jwt 的username 和 token 沒過期
                //就創建一個UsernamePasswordAuthenticationToken
                // 並將其設置到Spring Security的Security上下文中，以確保用戶已成功驗證。

                String role = jwtService.extractRole(jwt); // BUYER / SELLER
                System.out.println(role);

                List<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );
                System.out.println("author "+ authorities);


                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );


                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
            filterChain.doFilter(request,response);
        }
    }
}
