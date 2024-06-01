package com.shop.dto;

import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String oldpassword;
    private String newpassword;
    private String renewpassword;

    public UserDto(String username, String oldpassword, String newpassword, String renewpassword) {
        this.username = username;
        this.oldpassword = oldpassword;
        this.newpassword = newpassword;
        this.renewpassword = renewpassword;
    }
}
