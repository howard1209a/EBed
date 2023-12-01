package org.howard1209a.user.mapper;

import org.apache.ibatis.annotations.Select;
import org.howard1209a.user.pojo.User;

public interface InfoMapper {
    @Select("select * from user where user_id = #{userId}")
    public User queryUserById(Long userId);
}
