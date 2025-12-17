package com.shop.config;

import com.shop.component.JwtAuthenticationEntryPoint;
import com.shop.component.jwtAccessDeniedHandler;
import com.shop.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthentication;
    @Autowired
    private jwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);//獲取詳細訊息
        authProvider.setPasswordEncoder(passwordEncoder());//密碼加密器
        return authProvider;
    }

    //身分驗證管理器
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //引用JWT的filter 所綁定
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)//禁止跨站csrf
                /*對所有訪問HTTP端點的HttpServletRequest進行限制*/
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login/**","/Read/**",
                                "/Api/Payment/**")
                        //指定上述路徑，允許所有用戶進入
                        .permitAll()
                        //其他請求則需要透過身分驗證
                        .anyRequest().authenticated())
                //.sessionManagement(sess ->
                //        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(exceptions -> exceptions
                        //用來解決認證過的使用者訪問無權限資源時的異常
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        //用來解決匿名使用者訪問無權限資源時的異常
                        .authenticationEntryPoint(jwtAuthentication))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
