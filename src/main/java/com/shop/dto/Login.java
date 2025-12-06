package com.shop.dto;

import com.shop.entity.UserLevel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class Login implements UserDetails {
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserLevel role;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername(){
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }


    //以下方法 return false 為不被授權
    //用來判斷使用者的帳戶是否過期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    //用來判斷使用者的帳戶是否被鎖定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    //用來判斷使用者的認證信息是否過期，例如密碼是否過期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    //用來判斷使用者是否啟用，如果使用者已被禁用
    @Override
    public boolean isEnabled() {
        return true;
    }

}
