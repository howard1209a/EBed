package org.howard1209a.user.mapper;

import org.apache.ibatis.annotations.Select;
import org.howard1209a.user.pojo.User;

public interface LoginMapper {
    @Select("select * from user where user_id = #{userId}")
    public User findUserById(Long userId);
}
