package org.howard1209a.user.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.howard1209a.user.pojo.User;
import org.howard1209a.user.pojo.dto.LoginDto;

public interface RegisterMapper {
    @Select("select * from user where user_name = #{userName}")
    User findByUserName(@Param("userName") String userName);

    @Select("select * from user where email = #{email}")
    User findByEmail(@Param("email") String email);

    @Insert("insert into user (user_id,user_name,password,email) values (#{userId},#{userName},#{password},#{email})")
    void saveUser(User user);
}
