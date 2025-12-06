package com.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.dto.Login;
import com.shop.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where username=#{name}")
    Login findUserByUsername(String name);

    @Insert("insert into user(username,password,create_time,update_time,role) values(#{username},#{password},now(),now(),#{role})")
    void register(String username, String password,String role);

    @Select("select user.id from user where username=#{username}")
    int findIdbyName(String username);
    @Select("select user.username from user where id=#{id}")
    String selectNameById(int id);
}
