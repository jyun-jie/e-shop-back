package com.shop.entity;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

//負責 user的資訊(包括帳密及其詳細資料)
@Data
@Table(name = "user")
public class User  {

    @Id
    private int id ;
    private String username;
    private String password;
    private String nickname;//名稱
    private String email;//郵件
    private String userPic;//頭像
    private LocalDateTime createTime;//創建时间
    private LocalDateTime updateTime;//更新时间
    @Enumerated(EnumType.STRING)
    private UserLevel userLevel;


}
