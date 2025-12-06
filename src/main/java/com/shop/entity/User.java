package com.shop.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;


//負責 user的資訊(包括帳密及其詳細資料)
@Data
@Table(name = "user")
public class User {
    @TableId(type = IdType.AUTO)
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
